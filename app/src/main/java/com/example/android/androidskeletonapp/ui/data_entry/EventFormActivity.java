package com.example.android.androidskeletonapp.ui.data_entry;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.OrientationHelper;

import com.example.android.androidskeletonapp.BuildConfig;
import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.data.service.forms.EventFormService;
import com.example.android.androidskeletonapp.data.service.forms.FormField;
import com.example.android.androidskeletonapp.data.service.forms.RuleEngineService;
import com.example.android.androidskeletonapp.databinding.ActivityEnrollmentFormBinding;
import com.example.android.androidskeletonapp.ui.data_entry.field_type_holder.FormAdapter;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;

import org.apache.commons.lang3.tuple.Pair;
import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper;
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;
import org.hisp.dhis.rules.RuleEngine;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionAssign;
import org.hisp.dhis.rules.models.RuleActionHideField;
import org.hisp.dhis.rules.models.RuleActionShowError;
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
import java.util.TreeMap;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

import static android.text.TextUtils.isEmpty;

public class EventFormActivity extends AppCompatActivity {

    private final int CAMERA_RQ = 0;
    private final int CAMERA_PERMISSION = 0;

    private ActivityEnrollmentFormBinding binding;
    private FormAdapter adapter;
    private CompositeDisposable disposable;
    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private RuleEngine ruleEngine;
    private FormType formType;
    private String fieldWaitingImage;
    private String eventUid;
    GlobalClass globalVars;

    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, PROGRAM_STAGE_UID, ENROLLMENT_UID, TYPE
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid, String programUid, String programStageUid, String enrollmentUid,
                                               FormType type) {
        Intent intent = new Intent(context, EventFormActivity.class);
        intent.putExtra(IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(IntentExtra.PROGRAM_STAGE_UID.name(), programStageUid);
        intent.putExtra(IntentExtra.ENROLLMENT_UID.name(), enrollmentUid);
        intent.putExtra(IntentExtra.TYPE.name(), type.name());
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
        getSupportActionBar().setTitle(Sdk.d2().programModule().programStages()
                .byUid().eq(getIntent().getStringExtra(IntentExtra.PROGRAM_STAGE_UID.name()))
                .one().blockingGet().displayName());

        globalVars = (GlobalClass) getApplicationContext();
        eventUid = getIntent().getStringExtra(IntentExtra.EVENT_UID.name());

        formType = FormType.valueOf(getIntent().getStringExtra(IntentExtra.TYPE.name()));

        adapter = new FormAdapter(getValueListener(), getImageListener());
        binding.buttonEnd.setOnClickListener(this::finishEnrollment);
        binding.formRecycler.setAdapter(adapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(binding.formRecycler.getContext(), OrientationHelper.VERTICAL);
        binding.formRecycler.addItemDecoration(itemDecor);


        engineInitialization = PublishProcessor.create();

        if (EventFormService.getInstance().init(
                Sdk.d2(),
                eventUid,
                getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name()),
                getIntent().getStringExtra(IntentExtra.PROGRAM_STAGE_UID.name()),
                globalVars.getOrgUid().uid(),
                getIntent().getStringExtra(IntentExtra.ENROLLMENT_UID.name()) ))
            this.engineService = new RuleEngineService();

    }

    private FormAdapter.OnValueSaved getValueListener() {
        return (fieldUid, value) -> {

            TrackedEntityDataValueObjectRepository valueRepository =
                    Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                            .value(
                                    EventFormService.getInstance().getEventUid(),
                                    fieldUid
                            );

            String currentValue = valueRepository.blockingExists() ?
                    valueRepository.blockingGet().value() : "";
            if (currentValue == null)
                currentValue = "";

            try {
                if (!isEmpty(value) && !fieldUid.equalsIgnoreCase("EventDate")) {
                    valueRepository.blockingSet(value);
                } else {
                    valueRepository.blockingDeleteIfExist();
                }
            } catch (D2Error d2Error) {
                d2Error.printStackTrace();
            } finally {
                if (value != null && !value.equals(currentValue)) {
                    if (fieldUid.equalsIgnoreCase("EventDate") && !value.equals("")) {
                        try{
                            Date dateFromValue = DateFormatHelper.parseDateAutoFormat(value);
                            EventFormService.getInstance().saveEventDate(dateFromValue);
                        }
                        catch(ParseException ex){}
                    }
                    engineInitialization.onNext(true);
                }
            }
        };
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

    @Override
    protected void onResume() {
        super.onResume();
        disposable = new CompositeDisposable();

        disposable.add(
                Flowable.zip(
                        engineService.configure(Sdk.d2(),
                                getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name()),
                                EventFormService.getInstance().getEventUid()),
                        EventFormService.getInstance().isListingRendering(),
                        Pair::of
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                ruleEngineANDrendering -> {
                                    this.ruleEngine = ruleEngineANDrendering.getLeft();
                                    this.adapter.setListingRendering(ruleEngineANDrendering.getRight());
                                    engineInitialization.onNext(true);
                                },
                                Throwable::printStackTrace
                        )
        );

        disposable.add(
                engineInitialization
                        .flatMap(next ->
                                Flowable.zip(
                                        EventFormService.getInstance().getEventFormFields()
                                                .subscribeOn(Schedulers.io()),
                                        engineService.ruleEvent().flatMap(ruleEvent ->
                                                Flowable.fromCallable(() -> ruleEngine.evaluate(ruleEvent).call()))
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
                                fl.getOptionCode(), false,
                                fl.getObjectStyle()));
                        try {
                            Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                                    .value(
                                            EventFormService.getInstance().getEventUid(),
                                            fl.getUid()
                                    ).blockingSet(ruleEffect.data());
                        } catch (Exception e) {}
                    }

            }
            else if (ruleEffect.ruleAction() instanceof RuleActionHideField) {

                for (String key : fields.keySet())
                    if (key.equals(((RuleActionHideField) ruleAction).field())) {
                        FormField fl = fields.get(key);
                        fields.put(fl.getUid(), new FormField(
                                fl.getUid(), fl.getOptionSetUid(),
                                fl.getValueType(), fl.getFormLabel(), fl.getFormHint(),
                                "",
                                fl.getOptionCode(), false,
                                fl.getObjectStyle()));
                        if (Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                                .byEvent().eq(EventFormService.getInstance().getEventUid())
                                .byDataElement().eq(key).blockingGet().size() > 0) {
                            try {
                                Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                                        .value(
                                                EventFormService.getInstance().getEventUid(),
                                                fl.getUid()
                                        ).blockingDeleteIfExist();
                            } catch (Exception e) {
                            }
                        }
                    }
                fields.remove(((RuleActionHideField) ruleAction).field());

                /*
                fields.remove(((RuleActionHideField) ruleAction).field());
                for (String key : fields.keySet()) //For image options
                    if (key.equals(((RuleActionHideField) ruleAction).field()))
                        fields.remove(key);
                */
                /*
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
                    }
                for (String key : myList) {
                    fields.remove(key);
                }
                */
            }
            else if (ruleEffect.ruleAction() instanceof RuleActionShowWarning) {
                for (String key : fields.keySet())
                    if (key.equals(((RuleActionShowWarning) ruleAction).field())) {
                        FormField fl = fields.get(key);
                        fields.put(fl.getUid(),new FormField(
                                fl.getUid(), fl.getOptionSetUid(),
                                fl.getValueType(), fl.getFormLabel(), ((RuleActionShowWarning) ruleAction).content(),
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
        EventFormService.clear();
        super.onDestroy();
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
        if (formType == FormType.CREATE)
            EventFormService.getInstance().delete();
        else
            EventFormService.getInstance().rollBack();
        setResult(RESULT_CANCELED);
        finish();
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
                            Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                                    .value(eventUid, fieldWaitingImage).blockingSet(fileResourceUid);
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
