package com.sakurafish.pockettoushituryou.repository

import androidx.annotation.WorkerThread
import com.sakurafish.pockettoushituryou.data.db.entity.FavoriteFoods
import com.sakurafish.pockettoushituryou.data.db.entity.Foods
import com.sakurafish.pockettoushituryou.data.db.entity.OrmaDatabase
import com.sakurafish.pockettoushituryou.store.Action
import com.sakurafish.pockettoushituryou.store.Dispatcher
import com.sakurafish.pockettoushituryou.viewmodel.HostClass
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteFoodsRepository @Inject
internal constructor(
        private val orma: OrmaDatabase,
        private val dispatcher: Dispatcher) {

    @WorkerThread
    fun isFavorite(foodsId: Int): Boolean {
        return !orma.relationOfFavoriteFoods().selector().foodsEq(foodsId).isEmpty
    }

    @WorkerThread
    fun findAll(): List<Foods> {
        val foods = ArrayList<Foods>()
        orma.relationOfFavoriteFoods().selector().orderByCreatedAtDesc().toList()
                .map { favorite -> foods += favorite.foods }
        return foods
    }

    @ExperimentalCoroutinesApi
    @WorkerThread
    fun delete(foods: Foods, hostClass: HostClass) {
        orma.relationOfFavoriteFoods().deleter().foodsEq(foods.id).execute()
        dispatcher.launchAndDispatch(Action.FavoritesFoodsUpdated(hostClass))
    }

    @ExperimentalCoroutinesApi
    @WorkerThread
    fun save(foods: Foods, hostClass: HostClass) {
        orma.relationOfFavoriteFoods().deleter().foodsEq(foods.id).execute()
        orma.relationOfFavoriteFoods().inserter().execute(FavoriteFoods(foods, Date()))
        dispatcher.launchAndDispatch(Action.FavoritesFoodsUpdated(hostClass))
    }

    @WorkerThread
    fun toString(foodsId: Int): String {
        val favoriteFoods = orma.relationOfFavoriteFoods().selector().foodsEq(foodsId).value()
        return favoriteFoods.toString()
    }

    companion object {
        private val TAG = FavoriteFoodsRepository::class.java.simpleName
    }
}