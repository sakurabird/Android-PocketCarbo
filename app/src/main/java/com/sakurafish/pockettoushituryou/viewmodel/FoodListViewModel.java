package com.sakurafish.pockettoushituryou.viewmodel;

import android.content.Context;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;

import com.sakurafish.pockettoushituryou.data.db.entity.FavoriteFoods;
import com.sakurafish.pockettoushituryou.data.db.entity.Foods;
import com.sakurafish.pockettoushituryou.data.db.entity.Kinds;
import com.sakurafish.pockettoushituryou.repository.FavoriteFoodsRepository;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;
import com.sakurafish.pockettoushituryou.repository.KindsRepository;
import com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment.ListType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import timber.log.Timber;

import static com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment.ListType.NORMAL;

public final class FoodListViewModel extends BaseObservable {
    final static String TAG = FoodListViewModel.class.getSimpleName();

    private Context context;
    private KindsRepository kindsRepository;
    private FoodsRepository foodsRepository;
    private FavoriteFoodsRepository favoriteFoodsRepository;

    private List<Kinds> kindsList;
    private List<Foods> foodsList;

    private ListType listType;
    private int foodsVisibility;
    private int kindSpinnerVisibility;
    private int emptyMessageVisibility;

    @Inject
    FoodListViewModel(Context context,
                      KindsRepository kindsRepository,
                      FoodsRepository foodsRepository,
                      FavoriteFoodsRepository favoriteFoodsRepository) {
        this.context = context;
        this.kindsRepository = kindsRepository;
        this.foodsRepository = foodsRepository;
        this.favoriteFoodsRepository = favoriteFoodsRepository;

        this.kindsList = new ArrayList<>();
        this.foodsList = new ArrayList<>();

        this.listType = NORMAL;
        setFoodsVisibility(View.VISIBLE);
        setKindSpinnerVisibility(View.VISIBLE);
        setEmptyMessageVisibility(View.GONE);
    }

    public int getFoodsVisibility() {
        return foodsVisibility;
    }

    public void setFoodsVisibility(int foodsVisibility) {
        this.foodsVisibility = foodsVisibility;
    }

    public int getKindSpinnerVisibility() {
        return kindSpinnerVisibility;
    }

    public void setKindSpinnerVisibility(int kindSpinnerVisibility) {
        this.kindSpinnerVisibility = kindSpinnerVisibility;
    }

    public int getEmptyMessageVisibility() {
        return emptyMessageVisibility;
    }

    public void setEmptyMessageVisibility(int emptyMessageVisibility) {
        this.emptyMessageVisibility = emptyMessageVisibility;
    }

    public List<Kinds> getKindsList() {
        return this.kindsList;
    }

    public synchronized Single<List<FoodViewModel>> getFoodViewModelList(@IntRange(from = 1, to = 6) int typeId, int kindId, int sort) {
        this.listType = NORMAL;
        return foodsRepository.findFromLocal(typeId, kindId, sort)
                .map(foodsData -> {
                    kindsList.clear();
                    kindsList.addAll(foodsData.getKinds());
                    foodsList.clear();
                    foodsList.addAll(foodsData.getFoods());
                    Timber.tag(TAG).d("getFoodViewModelList local data loaded type:" + typeId + " kinds size:" + foodsData.getKinds().size() + " foods size:" + foodsData.getFoods().size());

                    return getFoodViewModels(foodsData.getFoods());
                });
    }

    public synchronized Single<List<FoodViewModel>> getFoodViewModelList(@Nullable String query) {
        this.listType = ListType.SEARCH_RESULT;
        return foodsRepository.findFromLocal(query)
                .map(foodsData -> {
                    kindsList.clear();
                    foodsList.clear();
                    foodsList.addAll(foodsData.getFoods());
                    Timber.tag(TAG).d(String.format("search foods size:%s query:%s", foodsData.getFoods().size(), query));

                    List<FoodViewModel> models = getFoodViewModels(foodsData.getFoods());
                    setViewsVisiblity(models);
                    return models;
                });
    }

    public synchronized Single<List<FoodViewModel>> getFoodViewModelListFavorites() {
        this.listType = ListType.FAVORITES;
        return favoriteFoodsRepository.findAllFromLocal()
                .map(favoriteFoodsList -> {
                    kindsList.clear();
                    foodsList.clear();
                    List<Foods> list = new ArrayList<>();
                    for (FavoriteFoods favoriteFoods : favoriteFoodsList) {
                        foodsList.add(favoriteFoods.getFoods());
                        list.add(favoriteFoods.getFoods());
                    }
                    Timber.tag(TAG).d(String.format("getFoodViewModelListFavorites local data loaded foods size:%s", favoriteFoodsList.size()));

                    List<FoodViewModel> models = getFoodViewModels(list);
                    setViewsVisiblity(models);
                    return models;
                });
    }

    @NonNull
    private synchronized List<FoodViewModel> getFoodViewModels(List<Foods> foodsList) {
        List<FoodViewModel> foodViewModels = new ArrayList<>();

        for (Foods foods : foodsList) {
            String kindName = kindsRepository.findName(foods.getKindId());
            foodViewModels.add(new FoodViewModel(this.context,
                    this.favoriteFoodsRepository, foods, kindName));
        }
        return foodViewModels;
    }

    private void setViewsVisiblity(List<FoodViewModel> foodViewModels) {
        if (foodViewModels.size() > 0) {
            setFoodsVisibility(View.VISIBLE);
            setKindSpinnerVisibility(this.listType == NORMAL ? View.VISIBLE : View.GONE);
            setEmptyMessageVisibility(View.GONE);
        } else {
            setFoodsVisibility(View.GONE);
            setKindSpinnerVisibility(View.GONE);
            setEmptyMessageVisibility(View.VISIBLE);
        }
    }
}
