package com.ping.app.presentation.ui.feature.map

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.ping.app.R
import com.ping.app.databinding.FragmentPingMapAddBinding
import com.ping.app.presentation.base.BaseFragment

class PingAddMapFragment :
    BaseFragment<FragmentPingMapAddBinding, PingMapViewModel>(R.layout.fragment_ping_map_add) {
    override val viewModel: PingMapViewModel by activityViewModels()
    override fun initView(savedInstanceState: Bundle?) {
        // flow
        // 현재 위치 찾을때까지 로딩바
        // 현재 위치 찾고나서 지도 뷰 보여줌(주소로 검색?도 해보자(심화기능))
        // 핑 찍을 곳에 longclick하면 핑 찍히게
        // longclick하면 dialog로 상세정보 설정하도록 하기
        // 상세정보 - 위치, 주최자, 날짜(핑 모집, 언제 볼건지), 인원, 참여가능한 사람(친구만, 전체)

    }
}