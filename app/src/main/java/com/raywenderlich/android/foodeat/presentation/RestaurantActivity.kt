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

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.raywenderlich.android.foodeat.R
import com.raywenderlich.android.foodeat.domain.model.Restaurant
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_restaurants.*
import org.koin.android.viewmodel.ext.android.viewModel

class RestaurantActivity : AppCompatActivity() {

  private val viewModel: RestaurantViewModel by viewModel()
  private val restaurantListAdapter = RestaurantListAdapter()

  private val querySubject = PublishSubject.create<String>()

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_restaurants)

    restaurantsList.adapter = restaurantListAdapter

    viewModel.restaurantsLiveData.observe(this, Observer {
      handleDataChange(it)
    })

    viewModel.uiLiveData.observe(this, Observer {
      handleUIChange(it)
    })

    viewModel.setQueryListener(querySubject)
  }

  private fun handleUIChange(resource: Resource<Unit>?) {
    when (resource) {
      is Resource.Success -> Toast.makeText(this, getString(R.string.results_fetched), Toast.LENGTH_SHORT).show()
    }
  }

  private fun handleDataChange(resource: Resource<List<Restaurant>>) {
    hideLoading()
    when (resource) {
      is Resource.Success -> restaurantListAdapter.updateRestaurants(resource.data)
      Resource.Loading -> showLoading()
      is Resource.Error -> showError(resource.message)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)

    val searchItem = menu.findItem(R.id.action_search)
    val searchView = searchItem?.actionView as SearchView

    searchView.queryHint = getString(R.string.search)
    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?) = true

      override fun onQueryTextChange(newText: String): Boolean {
        querySubject.onNext(newText)
        return true
      }
    })

    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_all -> {
        viewModel.getAllRestaurants()
      }
      R.id.action_top -> {
        viewModel.getTopRestaurants()
      }
      R.id.action_bottom -> {
        viewModel.getLowestRatedRestaurants()
      }
    }

    return super.onOptionsItemSelected(item)
  }

  private fun showLoading() {
    loadingIndicator.visibility = View.VISIBLE
  }

  private fun hideLoading() {
    loadingIndicator.visibility = View.GONE
  }

  private fun showError(message: String?) {
    Log.e(this.javaClass.simpleName, message ?: "Something went wrong!")
  }
}
