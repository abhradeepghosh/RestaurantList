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

package com.raywenderlich.android.foodeat.domain

import com.raywenderlich.android.foodeat.database.RestaurantDao
import com.raywenderlich.android.foodeat.domain.model.Restaurant
import io.reactivex.Completable

class SaveRestaurants(private val dataSource: RestaurantDao) {
  operator fun invoke(): Completable {
    return dataSource.insertRestaurants(defaultRestaurants)
  }

  private val defaultRestaurants = listOf(
      Restaurant(name = "Island Grill", rating = 4),
      Restaurant(name = "Flavoroso", rating = 5),
      Restaurant(name = "Green Curry", rating = 1),
      Restaurant(name = "El Pirata Porch", rating = 2),
      Restaurant(name = "Sweet Escape", rating = 2),
      Restaurant(name = "Maute Steak", rating = 3),
      Restaurant(name = "Salty Squid", rating = 4),
      Restaurant(name = "Bangalore Spices", rating = 3),
      Restaurant(name = "Pancake World", rating = 2),
      Restaurant(name = "Veganic Corner", rating = 2),
      Restaurant(name = "Masala", rating = 1),
      Restaurant(name = "Grassfed Grill", rating = 1),
      Restaurant(name = "Inside Burger", rating = 3),
      Restaurant(name = "Greenanic Smoothies", rating = 4),
      Restaurant(name = "Freddy's Stove", rating = 5),
      Restaurant(name = "Grandma's Sweets", rating = 2),
      Restaurant(name = "Spicella Spanish Kitchen", rating = 2),
      Restaurant(name = "Xin Chao Vietnamese", rating = 1),
      Restaurant(name = "Paterro's Kitchen", rating = 5),
      Restaurant(name = "Fishly", rating = 3),
      Restaurant(name = "Mediterra Seafood", rating = 4),
      Restaurant(name = "Street Deli", rating = 5),
      Restaurant(name = "Whispering Bamboo", rating = 1),
      Restaurant(name = "Pepper Stone", rating = 3)

  )
}