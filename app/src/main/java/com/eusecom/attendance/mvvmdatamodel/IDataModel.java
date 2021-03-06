package com.eusecom.attendance.mvvmdatamodel;

import android.support.annotation.NonNull;
import com.eusecom.attendance.models.Employee;
import com.eusecom.attendance.mvvmmodel.Language;
import com.google.firebase.database.DataSnapshot;

import java.util.List;
import rx.Observable;

import static com.eusecom.attendance.mvvmmodel.Language.LanguageCode;

public interface IDataModel {

    //fab methods

    @NonNull
    Observable<String> getSavedRxFBemployee(String emitedstring);

    //recyclerview methods
    @NonNull
    Observable<String> getObservableKeyFBeditUser(@NonNull final Employee employee);

    @NonNull
    Observable<List<Employee>> getObservableFBusersEmployee();

    @NonNull
    Observable<DataSnapshot> getObservableFBusers();

    @NonNull
    Observable<DataSnapshot> getObservableFBabsences();

    @NonNull
    Observable<List<Employee>> getObservableEmployees();

    //spinner methods

    @NonNull
    Observable<List<Language>> getObservableSupportedLanguages();

    @NonNull
    Observable<String> getObservableGreetingByLanguageCode(@NonNull final LanguageCode code);
}
