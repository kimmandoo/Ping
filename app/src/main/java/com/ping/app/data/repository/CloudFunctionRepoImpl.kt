package com.ping.app.data.repository

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.ping.app.domain.dao.CloudFunctionRepo

class CloudFunctionRepoImpl: CloudFunctionRepo {
    private var functions: FirebaseFunctions = Firebase.functions
}