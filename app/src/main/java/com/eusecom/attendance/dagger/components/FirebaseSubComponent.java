package com.eusecom.attendance.dagger.components;

import android.content.SharedPreferences;
import com.eusecom.attendance.AllEmpsAbsMvvmActivity;
import com.eusecom.attendance.dagger.modules.FirebaseSubModule;
import com.eusecom.attendance.dagger.scopes.FirebaseScope;
import dagger.Subcomponent;

@FirebaseScope
@Subcomponent(modules={ FirebaseSubModule.class })
public interface FirebaseSubComponent {

    void inject(AllEmpsAbsMvvmActivity activity);

    SharedPreferences sharedPreferences();

    @Subcomponent.Builder
    interface Builder extends SubcomponentBuilder<FirebaseSubComponent> {
        Builder activityModule(FirebaseSubModule module);
    }

}