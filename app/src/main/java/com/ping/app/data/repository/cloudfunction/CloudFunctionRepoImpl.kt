package com.ping.app.data.repository.cloudfunction

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class CloudFunctionRepoImpl: CloudFunctionRepo {
    private var functions: FirebaseFunctions = Firebase.functions
}