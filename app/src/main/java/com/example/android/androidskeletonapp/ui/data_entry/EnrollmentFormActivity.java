package com.example.android.androidskeletonapp.ui.data_entry;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.example.android.androidskeletonapp.BuildConfig;
import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.data.service.forms.EnrollmentFormService;
import com.example.android.androidskeletonapp.data.service.forms.FormField;
import com.example.android.androidskeletonapp.data.service.forms.RuleEngineService;
import com.example.android.androidskeletonapp.databinding.ActivityEnrollmentFormBinding;
import com.example.android.androidskeletonapp.ui.data_entry.field_type_holder.FormAdapter;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;

import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper;
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
import org.hisp.dhis.rules.RuleEngine;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionAssign;
import org.hisp.dhis.rules.models.RuleActionHideField;
import org.hisp.dhis.rules.models.RuleActionShowWarning;
import org.hisp.dhis.rules.models.RuleEffect;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

import static android.text.TextUtils.isEmpty;

public class EnrollmentFormActivity extends AppCompatActivity {

    private final int CAMERA_RQ = 0;
    private final int CAMERA_PERMISSION = 0;

    private ActivityEnrollmentFormBinding binding;
    private FormAdapter adapter;
    private CompositeDisposable disposable;
    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private RuleEngine ruleEngine;

    private String teiUid;
    private String fieldWaitingImage;
    GlobalClass globalVars;

    private enum IntentExtra {
        TEI_UID, PROGRAM_UID, OU_UID
    }

    public static Intent getFormActivityIntent(Context context, String teiUid, String programUid) {
        Intent intent = new Intent(context, EnrollmentFormActivity.class);
        intent.putExtra(IntentExtra.TEI_UID.name(), teiUid);
        intent.putExtra(IntentExtra.PROGRAM_UID.name(), programUid);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enrollment_form);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        globalVars = (GlobalClass) getApplicationContext();

        teiUid = getIntent().getStringExtra(IntentExtra.TEI_UID.name());

        adapter = new FormAdapter(getValueListener(), getImageListener());
        binding.buttonEnd.setOnClickListener(this::finishEnrollment);
        binding.formRecycler.setAdapter(adapter);

        engineInitialization = PublishProcessor.create();

        if (EnrollmentFormService.getInstance().init(
                Sdk.d2(),
                getIntent().getStringExtra(IntentExtra.TEI_UID.name()),
                getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name()),
                globalVars.getOrgUid().uid()))
            this.engineService = new RuleEngineService();

    }

    private FormAdapter.OnImageSelectionClick getImageListener() {
        return fieldUid -> {
            fieldWaitingImage = fieldUid;

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            } else {
                requestCamera();
            }
        };
    }

    private void requestCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri photoUri = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider",
                new File(FileResourceDirectoryHelper.getFileResourceDirectory(this), "tempFile.png"));
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takePicture, CAMERA_RQ);
    }

    private FormAdapter.OnValueSaved getValueListener() {
        return (fieldUid, value) -> {
            TrackedEntityAttributeValueObjectRepository valueRepository =
                    Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                            .value(
                                    fieldUid,
                                    getIntent().getStringExtra(IntentExtra.TEI_UID.name()
                                    )
                            );
            String currentValue = valueRepository.blockingExists() ?
                    valueRepository.blockingGet().value() : "";
            if (currentValue == null)
                currentValue = "";

            try {
                if (!isEmpty(value)) {
                    valueRepository.blockingSet(value);
                } else {
                    valueRepository.blockingDeleteIfExist();
                }
            } catch (D2Error d2Error) {
                d2Error.printStackTrace();
            } finally {
                if (value != null && !value.equals(currentValue)) {
                    if (fieldUid.equalsIgnoreCase("EnrollmentDate") && !value.equals("")) {
                        try{
                            Date dateFromValue = DateFormatHelper.parseDateAutoFormat(value);
                            Enrollment enrollment = Sdk.d2().enrollmentModule().enrollments()
                                    .uid(EnrollmentFormService.getInstance().getEnrollmentUid()).blockingGet();
                            if (enrollment.state() == State.TO_POST)
                                EnrollmentFormService.getInstance().saveEnrollmentDate(dateFromValue);
                        }
                        catch(ParseException ex){}
                    }
                    engineInitialization.onNext(true);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        disposable = new CompositeDisposable();

        disposable.add(
                engineService.configure(Sdk.d2(),
                        getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name()),
                        EnrollmentFormService.getInstance().getEnrollmentUid(),
                        null
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                ruleEngine -> {
                                    this.ruleEngine = ruleEngine;
                                    engineInitialization.onNext(true);
                                },
                                Throwable::printStackTrace
                        )
        );

        disposable.add(
                engineInitialization
                        .flatMap(next ->
                                Flowable.zip(
                                        EnrollmentFormService.getInstance().getEnrollmentFormFields()
                                                .subscribeOn(Schedulers.io()),
                                        engineService.ruleEnrollment().flatMap(ruleEnrollment ->
                                                Flowable.fromCallable(() -> ruleEngine.evaluate(ruleEnrollment).call()))
                                                .subscribeOn(Schedulers.io()),
                                        this::applyEffects
                                ))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                fieldData -> adapter.updateData(fieldData),
                                Throwable::printStackTrace
                        )
        );
    }

    private List<FormField> applyEffects(Map<String, FormField> fields,
                                         List<RuleEffect> ruleEffects) {

        for (RuleEffect ruleEffect : ruleEffects) {
            RuleAction ruleAction = ruleEffect.ruleAction();
            if (ruleEffect.ruleAction() instanceof RuleActionAssign){
                for (String key : fields.keySet())
                    if (key.equals(((RuleActionAssign) ruleAction).field())) {
                        FormField fl = fields.get(key);
                        fields.put(fl.getUid(),new FormField(
                                fl.getUid(), fl.getOptionSetUid(),
                                fl.getValueType(), fl.getFormLabel(), fl.getFormHint(),
                                ruleEffect.data(),
                                fl.getOptionCode(), fl.isEditable(),
                                fl.getObjectStyle()));
                        try {
                            Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                                    .value(
                                            EnrollmentFormService.getInstance().getEnrollmentUid(),
                                            fl.getUid()
                                    ).blockingSet(ruleEffect.data());
                        } catch (Exception e) {}
                    }
            }
            else if (ruleEffect.ruleAction() instanceof RuleActionHideField) {
                List<String> myList = new ArrayList<>();
                for (String key : fields.keySet())
                    if (key.equals(((RuleActionHideField) ruleAction).field())) {
                        FormField fl = fields.get(key);
                        fields.put(fl.getUid(), new FormField(
                                fl.getUid(), fl.getOptionSetUid(),
                                fl.getValueType(), fl.getFormLabel(), fl.getFormHint(),
                                null,
                                fl.getOptionCode(), false,
                                fl.getObjectStyle()));
                        myList.add(fl.getUid());
                        try {
                            Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                                    .value(
                                            EnrollmentFormService.getInstance().getEnrollmentUid(),
                                            fl.getUid()
                                    ).blockingDelete();
                        } catch (Exception e) {
                        }
                    }
                for (String key : myList) {
                    fields.remove(key);
                }
            }
            else if (ruleEffect.ruleAction() instanceof RuleActionShowWarning) {
                for (String key : fields.keySet())
                    if (key.equals(((RuleActionShowWarning) ruleAction).field())) {
                        FormField fl = fields.get(key);
                        fields.put(fl.getUid(),new FormField(
                                fl.getUid(), fl.getOptionSetUid(),
                                fl.getValueType(), fl.getFormLabel() , ((RuleActionShowWarning) ruleAction).content(),
                                fl.getValue(),
                                fl.getOptionCode(), fl.isEditable(),
                                fl.getObjectStyle()));
                    }
            }
        }

        return new ArrayList<>(fields.values());
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposable.clear();
    }

    @Override
    protected void onDestroy() {
        EnrollmentFormService.clear();
        super.onDestroy();
    }

    private void clearForm(View view){
        if (EnrollmentFormService.getInstance().getNewEnrollment() == true)
            EnrollmentFormService.getInstance().delete();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void finishEnrollment(View view) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (EnrollmentFormService.getInstance().getNewEnrollment() == true)
            EnrollmentFormService.getInstance().delete();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestCamera();
        } else {
            fieldWaitingImage = null;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case CAMERA_RQ:
                if (resultCode == RESULT_OK) {
                    File file = new File(
                            FileResourceDirectoryHelper.getFileResourceDirectory(this),
                            "tempFile.png"
                    );
                    if (file.exists()) {
                        try {
                            String fileResourceUid =
                                    Sdk.d2().fileResourceModule().fileResources()
                                            .blockingAdd(FileResizerHelper.resizeFile(file, FileResizerHelper.Dimension.MEDIUM));
                            Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                                    .value(fieldWaitingImage, teiUid).blockingSet(fileResourceUid);
                            engineInitialization.onNext(true);
                        } catch (D2Error d2Error) {
                            d2Error.printStackTrace();
                        } finally {
                            fieldWaitingImage = null;
                        }
                    }
                }
        }
    }
}
