package com.practice.moviedatabase.views.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.practice.moviedatabase.R
import com.practice.moviedatabase.views.details.MovieDetailsActivity
import com.practice.moviedatabase.views.details.MovieShortDetailsActivity
import com.practice.moviedatabase.utilities.ServerConstants
import com.practice.moviedatabase.views.main.adapters.MovieListAdapter
import com.practice.moviedatabase.base.ItemClickListener
import com.practice.moviedatabase.dal.db.DBManager
import com.practice.moviedatabase.models.Movie
import com.practice.moviedatabase.models.params.TopRatedMovieParams
import com.practice.moviedatabase.dal.networks.ApiClient
import com.practice.moviedatabase.dal.networks.ApiService
import com.practice.moviedatabase.dal.repositories.TopRatedMovieRepository
import com.practice.moviedatabase.models.Result
import com.practice.moviedatabase.utilities.ServerConstants.BASE_URL
import com.practice.moviedatabase.utilities.SystemAction
import com.practice.moviedatabase.views.main.viewmodels.TopRatedMovieViewModel
import com.practice.moviedatabase.views.main.viewmodels.factories.TopRatedViewModelFactory

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var viewModel : TopRatedMovieViewModel
    private lateinit var adapter : MovieListAdapter

    private var checked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initViews()
        viewModelObservers()
    }

    private fun initViews() {

        adapter = MovieListAdapter(this)
        adapter.setItemClickListener(this)

        val layoutManager = LinearLayoutManager(this)
        movieListRecyclerView.layoutManager = layoutManager
        movieListRecyclerView.adapter = adapter

        detailsViewSwitch.isChecked = false

        detailsViewSwitch.setOnCheckedChangeListener { _, isChecked ->
            checked = isChecked
        }
    }

    private fun viewModelObservers() {

        /*
         * Dependency Injected manually
         */

        val internetOn = SystemAction.isInternetOn(this)
        val apiService = ApiClient.getClient(BASE_URL).create(ApiService::class.java)
        val appDao = DBManager.getInstance(this.application)
        val repository = TopRatedMovieRepository(internetOn, apiService, appDao)

        viewModel = ViewModelProviders.of(this, TopRatedViewModelFactory(repository))
            .get(TopRatedMovieViewModel::class.java)

        viewModel.init(
            TopRatedMovieParams(getString(R.string.api_key), getString(R.string.language),
                getString(R.string.default_page), getString(R.string.sorted_by))
        )

        viewModel.topRateMovieLiveData.observe(this@MainActivity, Observer {
            handleMoviesData(it)
        })
    }

    private fun handleMoviesData(result: Result<List<Movie>>) {
        when (result.status) {
            Result.Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }

            Result.Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                adapter.setTopRatedMovie(result.data!!)
                adapter.notifyDataSetChanged()
            }

            Result.Status.ERROR -> {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemClicked(movie: Movie, outputDate: String?) {

        if (checked) {
            val intent = Intent(this, MovieDetailsActivity::class.java)
            intent.putExtra("backdrop_url", ServerConstants.BASE_IMAGE_URL + movie.backdropPath)
            startNextActivity(movie, outputDate, intent)
        } else {
            val intent = Intent(this, MovieShortDetailsActivity::class.java)
            startNextActivity(movie, outputDate, intent)
        }
    }

    private fun startNextActivity(movie: Movie, outputDate: String?, intent: Intent) {
        intent.putExtra("poster_url", ServerConstants.BASE_IMAGE_URL + movie.posterPath)
        intent.putExtra("title", movie.title)
        intent.putExtra("release_date", outputDate)
        intent.putExtra("vote_average", movie.voteAverage.toString())
        intent.putExtra("overview", movie.overview)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}