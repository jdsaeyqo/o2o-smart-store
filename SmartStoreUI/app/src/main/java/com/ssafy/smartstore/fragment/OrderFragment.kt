package com.ssafy.smartstore.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.*
import com.ssafy.smartstore.activity.MapActivity
import com.ssafy.smartstore.activity.MenuDetailActivity
import com.ssafy.smartstore.activity.ShoppingListActivity
import com.ssafy.smartstore.adapter.MenuAdapter
import com.ssafy.smartstore.dto.ProductDTO
import com.ssafy.smartstore.databinding.FragmentOrderBinding
import com.ssafy.smartstore.viewmodel.ProductViewModel

class OrderFragment : Fragment() {
    private lateinit var ctx : Context

    private lateinit var binding: FragmentOrderBinding

    private var menuList = mutableListOf<ProductDTO>()
    private lateinit var menuAdapter: MenuAdapter
    private val productViewModel : ProductViewModel by viewModels()

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    private lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    private lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10

    private val orderList = hashMapOf<ProductDTO,Int>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDistance.text = "매장과의 거리 측정중 입니다."

        initProductList()

            binding.btnShoppingList.setOnClickListener {
                val intent = Intent(ctx, ShoppingListActivity::class.java)
                intent.putExtra("orderList",orderList)
                startActivity(intent)

            }

        mLocationRequest =  LocationRequest.create().apply {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }
        if(checkPermissionForLocation(ctx)){
            startLocationUpdates()
        }

        binding.btnMap.setOnClickListener {
            val intent = Intent(ctx, MapActivity::class.java)
            startActivity(intent)
        }

    }

    private fun initProductList() {
        productViewModel.getProductList()

        productViewModel.productList.observe(viewLifecycleOwner){
            if(productViewModel.productList.value != null){
                menuList = productViewModel.productList.value!!
                initProductAdapter()
            }
        }


    }

    private fun initProductAdapter() {
        menuAdapter = MenuAdapter(ctx)
        menuAdapter.menuList = menuList

        menuAdapter.setItemClickListener(object : MenuAdapter.ItemClickListener{
            override fun onClick(view: View, product: ProductDTO, position: Int) {

                val intent = Intent(ctx, MenuDetailActivity::class.java)
                intent.putExtra("product",product)
                resultLauncher.launch(intent)

            }

        })

        binding.rvMenuList.layoutManager = GridLayoutManager(ctx,3)
        binding.rvMenuList.adapter = menuAdapter
    }


    private val resultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        if(it.resultCode == Activity.RESULT_OK){
            val intent = it.data
            val product = intent?.extras?.get("product") as ProductDTO
            val quantity = intent.extras?.get("quantity").toString().toInt()

            if(orderList[product] == null){
                orderList[product] = quantity
            }else{
                orderList[product] = orderList[product]!!+quantity
            }

        }
    }

    //     위치 권한이 있는지 확인하는 메서드
    private fun checkPermissionForLocation(context: Context): Boolean {
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    private fun startLocationUpdates() {

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if(checkPermission()){
            // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
            // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
            mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()!!)
        }

    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        mLastLocation = location


        val lat1 = mLastLocation.latitude
        val lon1 =  mLastLocation.longitude

        val lat2 = 36.10830144233874
        val lon2 = 128.41827450414362

        val x = (Math.cos(lat1) * 6400 * 2 * 3.14 / 360) * Math.abs(lon1-lon2)
        val y = 111 * Math.abs(lat1 - lat2)

        val d = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y,2.0))

        val distance = (d * 1000).toInt()

        Log.d("onLocationChanged", "onLocationChanged: $distance")

        binding.tvDistance.text = "매장과의 거리가 ${distance}m 입니다."

    }

    private fun checkPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }


}