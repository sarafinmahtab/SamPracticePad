package com.practice.moviedatabase.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practice.moviedatabase.R
import com.practice.moviedatabase.Urls
import com.practice.moviedatabase.base.ItemClickListener
import com.practice.moviedatabase.models.Result
import com.practice.moviedatabase.models.TopRatedMovie
import java.text.SimpleDateFormat
import java.util.*

class MovieListAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val movieOddItem: Int = 0
    private val movieEvenItem: Int = 1

    private var topRatedMovie: TopRatedMovie? = null
    private var movieList: List<Result>? = null

    private val inputDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outputDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    private lateinit var listener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val oddView = LayoutInflater.from(context).inflate(R.layout.layout_movie_item_odd, parent, false)
        val evenView = LayoutInflater.from(context).inflate(R.layout.layout_movie_item_even, parent, false)

        return when (viewType) {
            movieOddItem -> MovieOddListViewHolder(oddView)
            else -> MovieEvenListViewHolder(evenView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder.itemViewType == movieOddItem) {

            val oddViewHolder: MovieOddListViewHolder = holder as MovieOddListViewHolder

            val result = movieList!![position]
            val formattedDate = inputDateFormat.parse(result.releaseDate)
            val outputDate = outputDateFormat.format(formattedDate)

            oddViewHolder.movieTitleTextView.text = result.title
            oddViewHolder.releasedDateTextView.text = String.format("Released : %s", outputDate)

            Glide.with(context)
                .load(Urls.BASE_IMAGE_URL + result.posterPath)
                .placeholder(R.drawable.ic_movie_poster)
                .into(oddViewHolder.moviePosterImageView)

            oddViewHolder.itemView.setOnClickListener {
                listener.onItemClicked(result, outputDate)
            }

        } else {

            val evenViewHolder: MovieEvenListViewHolder = holder as MovieEvenListViewHolder

            val result = movieList!![position]
            val formattedDate = inputDateFormat.parse(result.releaseDate)
            val outputDate = outputDateFormat.format(formattedDate)

            evenViewHolder.movieTitleTextView.text = result.title
            evenViewHolder.releasedDateTextView.text = String.format("Released : %s", outputDate)

            Glide.with(context)
                .load(Urls.BASE_IMAGE_URL + result.posterPath)
                .placeholder(R.drawable.ic_movie_poster)
                .into(evenViewHolder.moviePosterImageView)

            evenViewHolder.itemView.setOnClickListener {
                listener.onItemClicked(result, outputDate)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position.and(1) == 1) movieOddItem else movieEvenItem
    }

    override fun getItemCount(): Int {
        return movieList!!.size
    }

    fun setTopRatedMovie(topRatedMovie: TopRatedMovie) {
        this.topRatedMovie = topRatedMovie

        if (topRatedMovie.results == null) {
            this.movieList = ArrayList()
        } else {
            this.movieList = topRatedMovie.results
        }
    }

    fun setItemClickListener(listener: ItemClickListener) {
        this.listener = listener
    }
}

class MovieOddListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val moviePosterImageView: ImageView = itemView.findViewById(R.id.moviePosterImageView)

    val movieTitleTextView: TextView = itemView.findViewById(R.id.movieTitleTextView)
    val releasedDateTextView: TextView = itemView.findViewById(R.id.movieReleasedTextView)
}

class MovieEvenListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val moviePosterImageView: ImageView = itemView.findViewById(R.id.moviePosterImageView)

    val movieTitleTextView: TextView = itemView.findViewById(R.id.movieTitleTextView)
    val releasedDateTextView: TextView = itemView.findViewById(R.id.movieReleasedTextView)
}
