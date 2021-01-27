package com.panaceasoft.psbuyandsell.ui.item.map.mapFilter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.databinding.DataBindingUtil;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.databinding.ActivityMapFilteringBinding;
import com.panaceasoft.psbuyandsell.ui.common.PSAppCompactActivity;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.MyContextWrapper;

public class MapFilteringActivity extends PSAppCompactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMapFilteringBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_map_filtering);

        // Init all UI
        initUI(binding);
    }
    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String CURRENT_LANG_CODE = preferences.getString(Constants.LANGUAGE_CODE, Config.DEFAULT_LANGUAGE);
        String CURRENT_LANG_COUNTRY_CODE = preferences.getString(Constants.LANGUAGE_COUNTRY_CODE, Config.DEFAULT_LANGUAGE_COUNTRY_CODE);

        super.attachBaseContext(MyContextWrapper.wrap(newBase, CURRENT_LANG_CODE, CURRENT_LANG_COUNTRY_CODE, true));
    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Utils.psLog("Inside Result MainActivity");
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//
//
//    }
//        Fragment fragment = getSupportFragmentManager().findFragmentBDouble.valueOf(lngValue)yId(R.id.content_frame);
//        fragment.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        navigationController.navigateBackToSearchFromMapFiltering(this, null);
    }

    //region Private Methods

    private void initUI(ActivityMapFilteringBinding binding) {
        initToolbar(binding.toolbar, getResources().getString(R.string.map_filter__map_title));
        setupFragment(new MapFilteringFragment());
    }
}
