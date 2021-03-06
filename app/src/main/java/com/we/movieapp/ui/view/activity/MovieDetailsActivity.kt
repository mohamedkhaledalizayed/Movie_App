package com.we.movieapp.ui.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bumptech.glide.Glide
import com.we.movieapp.R
import com.we.movieapp.domain.entities.MovieEntity
import com.we.movieapp.ui.EXTRA_MOVIE
import com.we.movieapp.ui.entities.MovieUiModel
import com.we.movieapp.ui.setVisibility
import com.we.movieapp.ui.startDetailsScreen
import com.we.movieapp.ui.utils.ViewState
import com.we.movieapp.ui.view.adapter.HomeMovieAdapter
import com.we.movieapp.ui.view.adapter.RecommendationMoviesAdapter
import com.we.movieapp.ui.view.adapter.SimilarMoviesAdapter
import com.we.movieapp.ui.viewmodel.homeviewmodel.HomeViewModel
import com.we.movieapp.ui.viewmodel.moviedetailviewmodel.MovieDetailsViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_movie_details.*
import javax.inject.Inject

class MovieDetailsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var movieDetailsViewModel: MovieDetailsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var similarMoviesAdapter: SimilarMoviesAdapter
    @Inject
    lateinit var recommendationMoviesAdapter: RecommendationMoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        supportActionBar?.title = intent.getStringExtra(EXTRA_MOVIE)

        configUI()
        movieDetailsViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieDetailsViewModel::class.java)

        movieDetailsViewModel.getMovieDetails(intent.getIntExtra(EXTRA_MOVIE,38700)).observe(this, Observer<ViewState<MovieUiModel>> {movieViewState ->

            when (movieViewState.status) {
                ViewState.Status.LOADING -> {
                    setVisibility(isLoading = true)
                    Log.e("fghj","hjhjh")
                }

                ViewState.Status.SUCCESS -> {
                    setVisibility(isLoading = false)
                    Log.e("fghj",movieViewState.data?.title)
                    movie_details_title.text = movieViewState.data?.title
//                    homeMovieAdapter.submitList(movieViewState.data)

                    val IMAGE_URL = "https://image.tmdb.org/t/p/w200/"
                    Glide.with(this@MovieDetailsActivity)
                        .load(IMAGE_URL + movieViewState.data?.posterPath)
                        .into(movie_details_image)
                    movie_details_title.text = movieViewState.data?.title
                    movie_details_desc.text = movieViewState.data?.overview
                    movie_details_language.text = getString(R.string.language, movieViewState.data?.originalLanguage)
                    movie_details_release_date.text = getString(R.string.release_date, movieViewState.data?.releaseDate)
                    getSimilarMovies()
                    getRecommendationMovies()

                }

                ViewState.Status.ERROR -> {
                    setVisibility(isLoading = false, errorMessage = movieViewState.message)
                    Log.e("fghj",movieViewState.message)

                }
            }



        })



    }

    private fun getRecommendationMovies() {
        movieDetailsViewModel.getRecommendationMovies(38700).observe(this, Observer<ViewState<List<MovieUiModel>>> {movieViewState->

            when (movieViewState.status) {
                ViewState.Status.LOADING -> {
                    setVisibility(isLoading = true)
                }

                ViewState.Status.SUCCESS -> {
                    setVisibility(isLoading = false)
                    recommendationMoviesAdapter.submitList(movieViewState.data)

                }

                ViewState.Status.ERROR -> {
                    setVisibility(isLoading = false, errorMessage = movieViewState.message)
                }
            }

        })
    }

    private fun getSimilarMovies() {

        movieDetailsViewModel.getSimilarMovies(38700).observe(this, Observer<ViewState<List<MovieUiModel>>> {movieViewState->

            when (movieViewState.status) {
                ViewState.Status.LOADING -> {
                    setVisibility(isLoading = true)
                }

                ViewState.Status.SUCCESS -> {
                    setVisibility(isLoading = false)
                    similarMoviesAdapter.submitList(movieViewState.data)

                }

                ViewState.Status.ERROR -> {
                    setVisibility(isLoading = false, errorMessage = movieViewState.message)
                }
            }

        })

    }


    private fun configUI() {

        similar_movies_recycler_view_list.apply {
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = similarMoviesAdapter
        }

        recommendation_movies_recycler_view_list.apply {
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = recommendationMoviesAdapter
        }

        similarMoviesAdapter.onMovieClickListener = object :
            SimilarMoviesAdapter.OnMovieClickListener {
            override fun setOnMovieClickListener(movieId: Int) {
//                startDetailsScreen(movieId)
            }
        }

        recommendationMoviesAdapter.onMovieClickListener = object :
            RecommendationMoviesAdapter.OnMovieClickListener {
            override fun setOnMovieClickListener(movieId: Int) {
//                startDetailsScreen(movieId)
            }
        }

    }

}
