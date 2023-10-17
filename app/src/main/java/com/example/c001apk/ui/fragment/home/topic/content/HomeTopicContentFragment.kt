package com.example.c001apk.ui.fragment.home.topic.content

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.c001apk.R
import com.example.c001apk.databinding.FragmentTopicContentBinding
import com.example.c001apk.ui.fragment.home.HomeFragment
import com.example.c001apk.ui.fragment.minterface.IOnBottomClickContainer
import com.example.c001apk.ui.fragment.minterface.IOnBottomClickListener
import com.example.c001apk.util.LinearItemDecoration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val URL = "url"
private const val TITLE = "title"

class HomeTopicContentFragment : Fragment(), IOnBottomClickListener {

    private lateinit var binding: FragmentTopicContentBinding
    private val viewModel by lazy { ViewModelProvider(this)[HomeTopicContentViewModel::class.java] }
    private lateinit var url: String
    private lateinit var title: String
    private lateinit var mAdapter: HomeTopicContentAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private var firstCompletelyVisibleItemPosition = 0
    private var lastVisibleItemPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(URL)!!
            title = it.getString(TITLE)!!
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeTopicContentFragment().apply {
                arguments = Bundle().apply {
                    putString(URL, param1)
                    putString(TITLE, param2)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopicContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initView()
        initRefresh()
        initScroll()

        viewModel.topicDataLiveData.observe(viewLifecycleOwner) { result ->
            val data = result.getOrNull()
            if (!data.isNullOrEmpty()) {
                if (viewModel.isRefreshing)
                    viewModel.topicDataList.clear()
                if (viewModel.isRefreshing || viewModel.isLoadMore)
                    for (element in data) {
                        if (element.entityTemplate == "feed" || element.entityType == "topic" || element.entityType == "product")
                            viewModel.topicDataList.add(element)
                    }
                mAdapter.notifyDataSetChanged()
                viewModel.isLoadMore = false
                viewModel.isRefreshing = false
                binding.swipeRefresh.isRefreshing = false
            } else {
                viewModel.isEnd = true
                viewModel.isLoadMore = false
                viewModel.isRefreshing = false
                binding.swipeRefresh.isRefreshing = false
                result.exceptionOrNull()?.printStackTrace()
            }
        }

    }

    private fun initScroll() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastVisibleItemPosition == viewModel.topicDataList.size - 1
                    ) {
                        if (!viewModel.isEnd) {
                            viewModel.isLoadMore = true
                            viewModel.page++
                            viewModel.getTopicData()
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition()
                firstCompletelyVisibleItemPosition =
                    mLayoutManager.findFirstCompletelyVisibleItemPosition()
            }
        })
    }

    private fun initRefresh() {
        //binding.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.black)
        /*binding.swipeRefresh.setColorSchemeColors(
            ThemeUtils.getThemeAttrColor(
                requireActivity(),
                rikka.preference.simplemenu.R.attr.colorPrimary
            )
        )*/
        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }
    }

    private fun initView() {
        val space = resources.getDimensionPixelSize(R.dimen.normal_space)
        mAdapter = HomeTopicContentAdapter(requireActivity(), viewModel.topicDataList)
        mLayoutManager = LinearLayoutManager(activity)
        binding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
            if (itemDecorationCount == 0)
                addItemDecoration(LinearItemDecoration(space))
        }
    }

    private fun initData() {
        if (viewModel.topicDataList.isEmpty())
            refreshData()
    }

    private fun refreshData() {
        binding.swipeRefresh.isRefreshing = true
        viewModel.isEnd = false
        viewModel.isRefreshing = true
        viewModel.isLoadMore = false
        viewModel.url = url
        viewModel.title = title
        lifecycleScope.launch {
            delay(500)
            viewModel.getTopicData()
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as IOnBottomClickContainer).controller = this
    }

    override fun onReturnTop() {
        if (HomeFragment.current == 3) {
            if (firstCompletelyVisibleItemPosition == 0)
                refreshData()
            else {
                binding.recyclerView.scrollToPosition(0)
                refreshData()
            }
        }
    }

}