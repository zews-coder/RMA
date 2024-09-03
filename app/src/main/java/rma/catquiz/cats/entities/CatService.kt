package rma.catquiz.cats.entities

import kotlinx.coroutines.flow.Flow
import rma.catquiz.cats.api.dto.ResultDto
import rma.catquiz.cats.api.interfaces.ICatListInterface
import rma.catquiz.cats.api.interfaces.IResultsInterface
import rma.catquiz.cats.entities.cat.Cat
import rma.catquiz.cats.entities.cat.CatDao
import rma.catquiz.cats.entities.image.CatGallery
import rma.catquiz.cats.entities.image.CatGalleryDao
import javax.inject.Inject

class CatService @Inject constructor(
    private val catDao: CatDao,
    private val catGalleryDao: CatGalleryDao,
    private val catApi: ICatListInterface,
    private val resultsApi: IResultsInterface
) {
    suspend fun fetchAllCatsFromApi() {
        catDao.insertAllCats(cats = catApi.getAllCats())
    }

    fun getAllCatsFlow(): Flow<List<Cat>> = catDao.getAllCats()

    fun getCatByIdFlow(id: String): Flow<Cat> = catDao.getCatById(id)

    fun getAllCatImagesByIdFlow(id: String): Flow<List<String>> = catGalleryDao.getAllImagesForId(id)

    suspend fun getAllCatsPhotosApi(id: String): List<CatGallery> {
        val images = catApi.getAllCatsPhotos(id).map { it.copy(id = id) }
        catGalleryDao.insertAllGalleryCats(cats = images)
        return images
    }

    suspend fun fetchAllResultsForCategory(category: Int): List<ResultDto> {
        return resultsApi.getAllResultsForCategory(category)
    }

    suspend fun postResult(nickname: String, result:Float, category: Int) {
        val dto = ResultDto(nickname,result,category)
        resultsApi.postResult(dto)
    }

}
