package com.example.android.androidskeletonapp.data.service.forms;

import android.text.TextUtils;
import android.widget.EditText;

import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

public class EnrollmentFormService {

    private D2 d2;
    private EnrollmentObjectRepository enrollmentRepository;
    private static List<TrackedEntityAttributeValue> enrollmentDataBackup;
    private static EnrollmentFormService instance;
    private final Map<String, FormField> fieldMap;
    private Boolean newEnrollment;
    GlobalClass globalVars;

    private EnrollmentFormService() {
        fieldMap = new LinkedHashMap<>();
    }

    public static EnrollmentFormService getInstance() {
        if (instance == null)
            instance = new EnrollmentFormService();

        return instance;
    }

    public boolean init(GlobalClass globalVars, D2 d2, String teiUid, String programUid) {
        this.d2 = d2;
        this.globalVars = globalVars;
        try {
            Enrollment enrollment = d2.enrollmentModule().enrollments()
                    .byProgram().eq(programUid)
                    .byTrackedEntityInstance().eq(teiUid)
                    .byStatus().eq(EnrollmentStatus.ACTIVE)
                    .one().blockingGet();
            if (enrollment == null) {
                String enrollmentUid = d2.enrollmentModule().enrollments().blockingAdd(
                        EnrollmentCreateProjection.builder()
                                .organisationUnit(globalVars.getOrgUid().uid())
                                .program(programUid)
                                .trackedEntityInstance(teiUid)
                                .build()
                );
                enrollmentRepository = d2.enrollmentModule().enrollments().uid(enrollmentUid);
                enrollmentRepository.setEnrollmentDate(getNowWithoutTime());
                enrollmentRepository.setIncidentDate(getNowWithoutTime());
                newEnrollment = true;
            }
            else {
                enrollmentRepository = d2.enrollmentModule().enrollments().uid(enrollment.uid());
                enrollmentDataBackup = d2.trackedEntityModule().trackedEntityAttributeValues()
                        .byTrackedEntityInstance().eq(teiUid).blockingGet();
                newEnrollment = false;
            }
            return true;
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
            return false;
        }
    }


    public Flowable<Map<String, FormField>> getEnrollmentFormFields() {
        if (d2 == null)
            return Flowable.error(
                    new NullPointerException("D2 is null. EnrollmentForm has not been initialized, use init() function.")
            );
        else
            fieldMap.clear();
            fieldMap.put("EnrollmentDate", new FormField(
                "EnrollmentDate", null, ValueType.DATE, globalVars.getTranslatedWord("Enrollment Date"), "",
                    "","","", enrollmentRepository.blockingExists() ?
                        DateFormatHelper.formatSimpleDate(enrollmentRepository.blockingGet().enrollmentDate()) : null,
                null, true,
                ObjectStyle.builder().build()));


            return Flowable.fromCallable(() ->
                    d2.programModule().programTrackedEntityAttributes()
                            .byProgram().eq(enrollmentRepository.blockingGet().program())
                            .orderBySortOrder(RepositoryScope.OrderByDirection.ASC).blockingGet()
            )
                    .flatMapIterable(programTrackedEntityAttributes -> programTrackedEntityAttributes)
                    .map(programAttribute -> {

                        TrackedEntityAttribute attribute = d2.trackedEntityModule().trackedEntityAttributes()
                                .uid(programAttribute.trackedEntityAttribute().uid())
                                .blockingGet();
                        TrackedEntityAttributeValueObjectRepository valueRepository =
                                d2.trackedEntityModule().trackedEntityAttributeValues()
                                        .value(programAttribute.trackedEntityAttribute().uid(),
                                                enrollmentRepository.blockingGet().trackedEntityInstance());

                        if (attribute.generated() && (valueRepository.blockingGet() == null || (valueRepository.blockingGet() != null &&
                                TextUtils.isEmpty(valueRepository.blockingGet().value())))) {
                            //get reserved value
                            String value = d2.trackedEntityModule().reservedValueManager()
                                    .blockingGetValue(programAttribute.trackedEntityAttribute().uid(),
                                            enrollmentRepository.blockingGet().organisationUnit());
                            valueRepository.blockingSet(value);
                        }
                        FormField field = new FormField(
                                attribute.uid(),
                                attribute.optionSet() != null ? attribute.optionSet().uid() : null,
                                attribute.valueType(),
                                String.format("%s%s", programAttribute.mandatory() ? "* " : "", attribute.displayName()),
                                attribute.displayDescription(), "","",programAttribute.mandatory() ? "1" : "",
                                valueRepository.blockingExists() ? valueRepository.blockingGet().value() : null,
                                null,
                                !attribute.generated(),
                                attribute.style()
                        );
                        fieldMap.put(programAttribute.trackedEntityAttribute().uid(), field);

                        return programAttribute;
                    })
                    .toList()
                    .toFlowable()
                    .map(list -> fieldMap);
    }

    public void saveCoordinates(double lat, double lon) {
        try {
            enrollmentRepository.setGeometry(GeometryHelper.createPointGeometry(lon, lat));
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }

    public void saveEnrollmentDate(Date enrollmentDate) {
        try {
            enrollmentRepository.setEnrollmentDate(enrollmentDate);
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }

    public void saveEnrollmentIncidentDate(Date incidentDate) {
        try {
            enrollmentRepository.setIncidentDate(incidentDate);
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }

    public void rollBack() {
        for (TrackedEntityAttributeValue tedv : enrollmentDataBackup) {
            TrackedEntityAttributeValueObjectRepository valueRepository =
                    Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                            .value(
                                    tedv.trackedEntityAttribute(),
                                    tedv.trackedEntityInstance()
                            );
            try {
                valueRepository.blockingSet(tedv.value());
            } catch (D2Error d2Error) {
                d2Error.printStackTrace();
            }
        }
    }

    public String getEnrollmentUid() {
        return enrollmentRepository.blockingGet().uid();
    }

    public Boolean getNewEnrollment(){
        return newEnrollment;
    }

    public void delete() {
        try {
            enrollmentRepository.blockingDelete();
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }

    public static void clear() {
        instance = null;
    }

    private Date getNowWithoutTime() {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime( new Date() );
        gc.set( Calendar.HOUR_OF_DAY, 0 );
        gc.set( Calendar.MINUTE, 0 );
        gc.set( Calendar.SECOND, 0 );
        gc.set( Calendar.MILLISECOND, 0 );
        return gc.getTime();
    }

}
