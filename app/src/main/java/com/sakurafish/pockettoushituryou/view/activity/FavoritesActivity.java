package com.sakurafish.pockettoushituryou.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.ActivityFavoritesBinding;
import com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment;

public class FavoritesActivity extends BaseActivity {

    private static final String TAG = FavoritesActivity.class.getSimpleName();

    ActivityFavoritesBinding binding;

    public static Intent createIntent(Context context) {
        return new Intent(context, FavoritesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favorites);
        initView();
        replaceFragment(FoodListFragment.newInstance(FoodListFragment.ListType.FAVORITES), R.id.content_view);
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.favorite));
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
}