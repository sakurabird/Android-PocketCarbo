package com.sakurafish.pockettoushituryou.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.FragmentFoodlistBinding;
import com.sakurafish.pockettoushituryou.databinding.ItemFoodlistBinding;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;
import com.sakurafish.pockettoushituryou.view.activity.SearchResultActivity;
import com.sakurafish.pockettoushituryou.view.adapter.ArrayRecyclerAdapter;
import com.sakurafish.pockettoushituryou.view.adapter.BindingHolder;
import com.sakurafish.pockettoushituryou.view.adapter.KindSpinnerAdapter;
import com.sakurafish.pockettoushituryou.viewmodel.FoodListViewModel;
import com.sakurafish.pockettoushituryou.viewmodel.FoodViewModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.sakurafish.pockettoushituryou.model.FoodsData.KINDS_ALL;

public class FoodListFragment extends BaseFragment {

    public static final String TAG = FoodListFragment.class.getSimpleName();

    private FragmentFoodlistBinding binding;
    private KindSpinnerAdapter kindSpinnerAdapter;
    private FoodListAdapter foodListAdapter;

    private int typeId;
    private int kindId = KINDS_ALL;
    private String query;

    @Inject
    FoodListViewModel viewModel;

    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    FoodsRepository foodsRepository;

    public static FoodListFragment newInstance(@IntRange(from = 1, to = 6) int type) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FoodListFragment newInstance(@Nullable String query) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SearchResultActivity.EXTRA_QUERY, query);
        fragment.setArguments(bundle);
        return fragment;
    }

    public FoodListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_foodlist, container, false);
        binding.setViewModel(viewModel);

        this.query = getArguments().getString(SearchResultActivity.EXTRA_QUERY, "");
        this.typeId = getArguments().getInt("type", 0);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        showFoods();
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.destroy();
        compositeDisposable.dispose();
    }

    private void initView() {
        foodListAdapter = new FoodListAdapter(getContext());
        binding.recyclerView.setAdapter(foodListAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        initKindsSpinner();
    }

    private void initKindsSpinner() {
        if (!this.query.isEmpty()) {
            kindId = KINDS_ALL;
            binding.spinner.setVisibility(View.GONE);
            return;
        }

        // Creating foodListAdapter for spinner
        kindSpinnerAdapter = new KindSpinnerAdapter(getActivity());
        binding.spinner.setAdapter(kindSpinnerAdapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    kindId = KINDS_ALL;
                } else {
                    kindId = viewModel.getKindsList().get(position - 1).id;
                }
                showFoods();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showFoods() {
        Timber.tag(TAG).d("showFoods start");
        if (this.query.isEmpty()) {
            Disposable disposable = viewModel.getFoodViewModelList(typeId, kindId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::renderFoods,
                            throwable -> Timber.tag(TAG).e(throwable, "Failed to show foods.")
                    );
            compositeDisposable.add(disposable);
        } else {
            Disposable disposable = viewModel.getFoodViewModelList(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::renderFoods,
                            throwable -> Timber.tag(TAG).e(throwable, "Failed to show foods.")
                    );
            compositeDisposable.add(disposable);
        }
    }

    private void renderFoods(List<FoodViewModel> foodViewModels) {
        Timber.tag(TAG).d("renderFoods start foodViewModels.size" + foodViewModels.size());
        if (binding.recyclerView.getLayoutManager() == null) {
            LinearLayoutManager lm = new LinearLayoutManager(getContext());
            binding.recyclerView.setLayoutManager(lm);
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(0, 0);
        if (binding.spinner.getVisibility() == View.VISIBLE) {
            kindSpinnerAdapter.setData(viewModel.getKindsList());
        }
        foodListAdapter.reset(foodViewModels);
    }

    private static class FoodListAdapter
            extends ArrayRecyclerAdapter<FoodViewModel, BindingHolder<ItemFoodlistBinding>> {

        FoodListAdapter(@NonNull Context context) {
            super(context);
        }

        @Override
        public BindingHolder<ItemFoodlistBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BindingHolder<>(getContext(), parent, R.layout.item_foodlist);
        }

        @Override
        public void onBindViewHolder(BindingHolder<ItemFoodlistBinding> holder, int position) {
            final FoodViewModel viewModel = getItem(position);
            if (viewModel.isExpanded()) {
                holder.binding.expandArrow.setSelected(true);
                holder.binding.expandableLayout.expand(true);
            } else {
                holder.binding.expandArrow.setSelected(false);
                holder.binding.expandableLayout.collapse(true);
            }

            // collapse or expand card
            viewModel.setOnClickListener(v -> {

                if (viewModel.isExpanded()) {
                    holder.binding.expandArrow.setSelected(false);
                    holder.binding.expandableLayout.collapse(true);
                    viewModel.setExpanded(false);
                } else {
                    holder.binding.expandArrow.setSelected(true);
                    holder.binding.expandableLayout.expand(true);
                    viewModel.setExpanded(true);
                }
            });

            holder.binding.setViewModel(viewModel);
            holder.binding.executePendingBindings();
        }
    }
}
