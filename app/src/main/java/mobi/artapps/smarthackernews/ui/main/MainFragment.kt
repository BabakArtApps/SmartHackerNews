package mobi.artapps.smarthackernews.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import mobi.artapps.smarthackernews.R
import mobi.artapps.smarthackernews.data.NetworkState
import mobi.artapps.smarthackernews.model.local.entity.News
import mobi.artapps.smarthackernews.model.remote.entity.NewsType
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var toolbar: Toolbar? = activity?.findViewById(R.id.toolbar)
        var spinner: Spinner? = toolbar?.findViewById(R.id.main_activity_spinner)
        val items = arrayOf("News", "Newest", "Ask", "Show", "Jobs")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, items)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val newsType = when (position) {
                    0 -> NewsType.NEWS
                    1 -> NewsType.NEWEST
                    2 -> NewsType.ASK
                    3 -> NewsType.SHOW
                    4 -> NewsType.JOBS
                    else -> NewsType.NEWS
                }
                mainViewModel.searchRepo(newsType)
            }

        }

        val swipeRefreshLayout = view?.findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
        swipeRefreshLayout?.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        swipeRefreshLayout?.setOnRefreshListener {
            mainViewModel.invalidateDataSource()
        }

        val recyclerView = view?.findViewById<RecyclerView>(R.id.main_fragment_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        newsAdapter = NewsAdapter()
        recyclerView?.adapter = newsAdapter

        mainViewModel.mAllNews.observe(this, Observer<PagedList<News>> {
            Log.d("Activity", "list: ${it?.size}")
            //showEmptyList(it?.size == 0)
            newsAdapter.submitList(it)
            swipeRefreshLayout?.isRefreshing = false
        })

        mainViewModel.networkErrors.observe(this, Observer<NetworkState> {
            //Toast.makeText(context, "\uD83D\uDE28 Wooops $it", Toast.LENGTH_LONG).show()
        })

        mainViewModel.searchRepo(NewsType.NEWS)
    }


}
