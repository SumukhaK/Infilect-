package com.ksa.infilect.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class Repository  @Inject constructor(remoteDataSource: RemoteDataSource,
                                      localDataSource: LocalDataSource) {

    val remoteDS = remoteDataSource
    val localDS = localDataSource
}