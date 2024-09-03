package rma.catquiz.cats.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import rma.catquiz.cats.entities.cat.Cat
import rma.catquiz.ui.ListInfo
import rma.catquiz.ui.SimpleInfo
import rma.catquiz.ui.TopBar

fun NavGraphBuilder.catDetailsScreen (
    route: String,
    navController: NavController,
    arguments: List<NamedNavArgument>
) = composable(route = route, arguments = arguments) { navBackStackEntry ->

    val catDetailsViewModel: CatDetailsViewModel = hiltViewModel(navBackStackEntry)
    val catState by catDetailsViewModel.catDetailsState.collectAsState()

    Surface(
        tonalElevation = 1.dp
    ) {
        Scaffold (
            topBar = {
                TopBar(onBackClick = {navController.navigateUp()})
            },
            content = { paddingValues ->
                CatDetailsScreen(
                    catState = catState,
                    paddingValues = paddingValues,
                    openGallery = {id ->  navController.navigate("images/${id}")}
                )

            }
        )
    }
}

@Composable
private fun CatDetailsScreen(
    catState: ICatDetailsContract.CatDetailsState,
    paddingValues: PaddingValues,
    openGallery: (String)-> Unit,
) {
    if (catState.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
    else if (catState.error != null) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val errorMessage = when (catState.error) {
                is ICatDetailsContract.CatDetailsState.DetailsError.DataUpdateFailed ->
                    "Failed to load. Error message: ${catState.error.cause?.message}."
            }

            Text(text = errorMessage, fontSize = 20.sp)
        }
    }
    else if (catState.data == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "There is no data for id ${catState.catId}",
                fontSize = 20.sp
            )
        }
    }
    else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
                SubcomposeAsyncImage(
                    modifier =  Modifier.fillMaxWidth(),
                    model = catState.data.image?.url ?: "",
                    contentDescription = null,
                    loading = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                )

                CatInformation(
                    catState = catState,
                    data = catState.data,
                    openGallery = openGallery
                )

            }
        }
    }
}

@Composable
private fun CatInformation(
    catState: ICatDetailsContract.CatDetailsState,
    data: Cat,
    openGallery: (String)-> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Button(
            onClick = {
                openGallery(catState.catId)
            }
        ) {
            Text(text = "Gallery")
        }

        SimpleInfo(
            title = "Race Of Cat",
            description = data.name
        )

        SimpleInfo(
            title = "Description",
            description = data.description
        )


        ListInfo(title = "Countries Of Origin", items = data.origin.replace(" ", "").split(","))


        ListInfo(title = "Temperament Traits", items = data.temperament.replace(" ", "").split(","))

        SimpleInfo(
            title = "Average Weight",
            description = data.weight.metric
        )

        SimpleInfo(
            title = "Life Span",
            description = data.life
        )
    }
}
