/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.foodeat.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raywenderlich.android.foodeat.domain.GetRestaurants
import com.raywenderlich.android.foodeat.domain.SaveRestaurants
import com.raywenderlich.android.foodeat.domain.model.Restaurant
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class RestaurantViewModel(getRestaurants: GetRestaurants, saveRestaurants: SaveRestaurants) : ViewModel() {

  private val restaurantsSource = getRestaurants()
  private val restaurantSource = getRestaurants().flatMapObservable { Observable.fromIterable(it) }

  private val _restaurantsLiveData = MutableLiveData<Resource<List<Restaurant>>>()
  val restaurantsLiveData: LiveData<Resource<List<Restaurant>>>
    get() = _restaurantsLiveData

  private val _uiLiveData = MutableLiveData<Resource<Unit>>()
  val uiLiveData: LiveData<Resource<Unit>>
    get() = _uiLiveData

  private val disposables = CompositeDisposable()

  init {
    saveRestaurants()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(::getAllRestaurants)
        .addTo(disposables)
  }

  fun getAllRestaurants() {
    _restaurantsLiveData.value = Resource.Loading

    restaurantsSource
        .subscribeOn(Schedulers.io())
        .map { Resource.Success(it) }
        .subscribe(_restaurantsLiveData::postValue)
        .addTo(disposables)
  }

  fun getTopRestaurants() {
    _restaurantsLiveData.value = Resource.Loading

    restaurantSource
        .take(5)
        .showResults()
  }

  fun getLowestRatedRestaurants() {
    _restaurantsLiveData.value = Resource.Loading

    restaurantSource
        .skipWhile { it.rating > 3 }
        .showResults()
  }

  fun setQueryListener(queryObservable: Observable<String>) {
    queryObservable
        .debounce(300, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .observeOn(AndroidSchedulers.mainThread())
        .map(::filterSource)
        .subscribe()
  }


  private fun filterSource(query: String) {
    Log.d(this::class.java.simpleName, "Search query: $query")

    _restaurantsLiveData.value = Resource.Loading

    restaurantSource
        .filter { res ->
          if (query.isEmpty()) return@filter true
          res.name.contains(query, true)
        }
        .showResults()
  }

  override fun onCleared() {
    super.onCleared()
    disposables.clear()
  }

  private fun Observable<Restaurant>.showResults() {
    this.toList()
        .subscribeOn(Schedulers.io())
        .map { Resource.Success(it) }
        .subscribe(_restaurantsLiveData::postValue)
        .addTo(disposables)

    this.ignoreElements()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          _uiLiveData.value = Resource.Success(Unit)
        }
        .addTo(disposables)
  }
}
