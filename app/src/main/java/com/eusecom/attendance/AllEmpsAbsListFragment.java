package com.eusecom.attendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.eusecom.attendance.models.Attendance;
import com.eusecom.attendance.realm.RealmEmployee;
import com.eusecom.attendance.rxbus.RxBus;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.flowables.ConnectableFlowable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class AllEmpsAbsListFragment extends Fragment {

    public AllEmpsAbsListFragment() {

    }
    private CompositeDisposable _disposables;
    private AllEmpsAbsRxRealmAdapter mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private RxBus _rxBus = null;

    @NonNull
    private CompositeSubscription mSubscription;

    @Inject
    SharedPreferences mSharedPreferences;

    //create mvvm without dagger2
    //@NonNull
    //private AllEmpsAbsMvvmViewModel mViewModel;

    @Inject
    AllEmpsAbsMvvmViewModel mViewModel;

    AlertDialog dialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create mvvm without dagger2
        //mViewModel = getAllEmpsAbsMvvmViewModel();

        _disposables = new CompositeDisposable();

        _rxBus = ((AttendanceApplication) getActivity().getApplication()).getRxBusSingleton();

        ConnectableFlowable<Object> tapEventEmitter = _rxBus.asFlowable().publish();

        _disposables
                .add(tapEventEmitter.subscribe(event -> {
                    if (event instanceof AllEmpsAbsListFragment.ClickFobEvent) {
                        Log.d("AllEmpsAbsActivity  ", " fobClick ");
                        String serverx = "AllEmpsAbsListFragment fobclick";
                        Toast.makeText(getActivity(), serverx, Toast.LENGTH_SHORT).show();


                    }
                    if (event instanceof RealmEmployee) {
                        String idemp = ((RealmEmployee) event).getKeyf();
                        RealmEmployee model= (RealmEmployee) event;

                        //Log.d("AllEmpsAbsListFragment ", icos);
                        //String serverx = "AllEmpsAbsListFragment shortclick";
                        //Toast.makeText(getActivity(), serverx, Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getActivity(), AbsenceActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("fromact", "1");
                        extras.putString("idemp", idemp);
                        i.putExtras(extras);
                        startActivity(i);

                    }

                }));

        _disposables
                .add(tapEventEmitter.publish(stream ->
                        stream.buffer(stream.debounce(1, TimeUnit.SECONDS)))
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(taps -> {
                            ///_showTapCount(taps.size()); OK
                        }));

        _disposables.add(tapEventEmitter.connect());
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_allempsabs, container, false);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.allempsabs_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AttendanceApplication) getActivity().getApplication()).getAllEmpsAbsComponent().inject(this);

        String umex = mSharedPreferences.getString("ume", "");
        mAdapter = new AllEmpsAbsRxRealmAdapter(Collections.<RealmEmployee>emptyList(), _rxBus, umex);
        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        mRecycler.setAdapter(mAdapter);

        //String serverx = "From fragment " + mSharedPreferences.getString("servername", "");
        //Toast.makeText(getActivity(), serverx, Toast.LENGTH_SHORT).show();


    }//end of onActivityCreated

    @Override
    public void onDestroy() {
        super.onDestroy();
        _disposables.dispose();
        mAdapter = new AllEmpsAbsRxRealmAdapter(Collections.<RealmEmployee>emptyList(), null, "01.2017");
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        _rxBus = null;
        mSubscription.unsubscribe();
        mSubscription.clear();


    }

    @Override
    public void onResume() {
        super.onResume();
        bind();
    }

    @Override
    public void onPause() {
        super.onPause();
        unBind();
    }

    private void bind() {
        mSubscription = new CompositeSubscription();


        mSubscription.add(mViewModel.getObservableFBusersRealmEmployee()
                .subscribeOn(Schedulers.computation())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(this::setRealmEmployees));

        mSubscription.add(mViewModel.getObservableDataSavedToRealm()
                .subscribeOn(Schedulers.computation())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(this::dataSavedToRealm));

        mSubscription.add(mViewModel.getObservableFromFBforRealm()
                .subscribeOn(Schedulers.computation())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(this::setAbsences));

        mSubscription.add(mViewModel.getObservableDataUpdatedRealm()
                .subscribeOn(Schedulers.computation())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(this::dataUpdatedToRealm));



    }



    private void unBind() {
        mAdapter.setRealmData(Collections.<RealmEmployee>emptyList());
        //is better to use mSubscription.clear(); by https://medium.com/@scanarch/how-to-leak-memory-with-subscriptions-in-rxjava-ae0ef01ad361
        mSubscription.unsubscribe();
        mSubscription.clear();
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dataSavedToRealm(@NonNull final String message) {
        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        //update absences to Realm
        String umex = mSharedPreferences.getString("ume", "");
        mViewModel.emitAbsencesFromFBforRealm(umex);
    }


    private void setRealmEmployees(@NonNull final List<RealmEmployee> realmemployees) {

        int maxemp = 6;

        if( realmemployees.size() > maxemp ) {
            getDialogLotOfEmp(realmemployees.size());
        }else {
            assert mRecycler != null;
            mAdapter.setRealmData(realmemployees);
            //save realmemployees to Realm
            mViewModel.emitRealmEmployeesToRealm(realmemployees);
        }
    }

    private void setAbsences(@NonNull final List<Attendance> absences) {

        //System.out.println("name " + absences.get(0).getUsname());
        mViewModel.emitUpdateRealmFromAbsences(absences);

    }

    private void dataUpdatedToRealm(@NonNull final List<RealmEmployee> realmemployees) {

        //String message = "name " + realmemployees.get(0).getUsername();
        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        mAdapter.setRealmData(realmemployees);
    }

    public static class ClickFobEvent {}

    //we use only for classic create mvvmviewmodel without dagger2
    @NonNull
    private AllEmpsAbsMvvmViewModel getAllEmpsAbsMvvmViewModel() {
        return ((AttendanceApplication) getActivity().getApplication()).getAllEmpsAbsMvvmViewModel();
    }


    private void getDialogLotOfEmp(int size){

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.lotofemp))
                .setMessage(getString(R.string.lotofempmes))
                .setPositiveButton(R.string.textok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {


                            }
                        })
                .show();

    }





}
