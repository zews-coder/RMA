package rma.catquiz.cats.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch
import rma.catquiz.R
import rma.catquiz.cats.entities.cat.Cat
import rma.catquiz.cats.entities.cat.CatWeight
import rma.catquiz.ui.AppIconButton
import rma.catquiz.ui.theme.CatapultTheme
import rma.catquiz.user.User

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.catsListScreen(
    route: String,
    navController: NavController,
    goToQuiz: () -> Unit,
) = composable(route = route) {
    val catsViewModel: CatsViewModel = hiltViewModel()
    val catsState by catsViewModel.catsState.collectAsState()

    val scope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    if (catsState.userData == User.EMPTY) {
        navController.navigate("login")
    } else {
        Surface(
            tonalElevation = 1.dp
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    UserInfoDrawer(
                        catsState = catsState,
                        catsViewModel = catsViewModel,
                        addNewUser = { navController.navigate("login?add-new-user=${true}") },
                        navigateToHistory = { navController.navigate("history") },
                        navigateToEdit = { navController.navigate("user/edit") },
                        leaderboard = { category ->
                            navController.navigate("quiz/leaderboard/${category}")
                        }
                    )
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Catapult",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            navigationIcon = {
                                AppIconButton(
                                    imageVector = Icons.Default.Menu,
                                    onClick = { scope.launch { drawerState.open() } }
                                )
                            },
                            actions = {
                                AppIconButton(
                                    imageVector = if (catsState.darkTheme) Icons.Outlined.LightMode else Icons.Filled.LightMode,
                                    onClick = {
                                        catsViewModel.setCatsEvent(
                                            ICatsContract.CatsListUIEvent.ChangeTheme(
                                                !catsState.darkTheme
                                            )
                                        )
                                    })
                            },
                            colors = TopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                scrolledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                navigationIconContentColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                                actionIconContentColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    //Dugme za pocetak kviza
                    floatingActionButton = {
                        LargeFloatingActionButton(
                            onClick = { goToQuiz() },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "QUIZ")
                                Spacer(modifier = Modifier.height(1.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.quiz100),
                                    contentDescription = "Floating action button."
                                )
                            }
                        }
                    },

                    content = {
                        CatsList(
                            catsState = catsState,
                            paddingValues = it,
                            eventPublisher = { uiEvent -> catsViewModel.setCatsEvent(uiEvent) },
                            onClick = { catInfoDetail -> navController.navigate("cats/${catInfoDetail.id}") }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun UserInfoDrawer(
    catsState: ICatsContract.CatsListState,
    catsViewModel: CatsViewModel,
    addNewUser: () -> Unit,
    navigateToHistory: () -> Unit,
    navigateToEdit: () -> Unit,
    leaderboard: (Int) -> Unit
) {
    BoxWithConstraints {
        val box = this
        ModalDrawerSheet(
            modifier = Modifier.width(box.maxWidth * 3 / 4)
        ) {

            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {

                Column {
                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 180.dp)
                    ) {
                        items(1) { user ->
                            UserItemDrawer(
                                user = catsState.userData,
                                catsState = catsState,
                                navigateToEdit = navigateToEdit,
                                catsViewModel = catsViewModel
                            )
                        }
                    }
                }
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(7.dp))

            Column {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        AppIconButton(
                            imageVector = Icons.Filled.Category,
                            onClick = addNewUser
                        )
                    },
                    label = {
                        Text(
                            text = "Quiz history",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    selected = false,
                    onClick = navigateToHistory
                )
            }

            Column {
                Text(
                    text = "Leaderboard",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        AppIconButton(
                            imageVector = Icons.Filled.Leaderboard,
                            onClick = {
                                leaderboard(3)
                            }
                        )
                    },
                    label = {
                        Text(
                            text = "Results",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    selected = false,
                    onClick = {
                        leaderboard(3)
                    }
                )
            }
        }
    }

}

@Composable
fun UserItemDrawer(
    user: User,
    catsState: ICatsContract.CatsListState,
    navigateToEdit: () -> Unit,
    catsViewModel: CatsViewModel,
) {

    NavigationDrawerItem(
        icon = {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                model = R.drawable.user,
                contentDescription = null,
                loading = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            )
        },
        label = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Column {
                    Text(
                        text = user.nickname,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                //LOGOUT button
                AppIconButton(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    onClick = {
                        catsViewModel.setCatsEvent(
                            ICatsContract.CatsListUIEvent.Logout(
                                user = catsState.userData
                            )
                        )
                    }
                )

            }
        },
        selected = true,
        onClick = navigateToEdit
    )
}

@Composable
fun CatsList(
    catsState: ICatsContract.CatsListState,
    paddingValues: PaddingValues,
    eventPublisher: (uiEvent: ICatsContract.CatsListUIEvent) -> Unit,
    onClick: (Cat) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {

        //SEARCH BAR
        TextField(
            value = catsState.searchText,
            onValueChange = { text ->
                eventPublisher(ICatsContract.CatsListUIEvent.SearchQueryChanged(query = text))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Search") },
            shape = CircleShape,
            leadingIcon = { AppIconButton(imageVector = Icons.Default.Search, onClick = { }) }
        )

        if (catsState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else if (catsState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val errorMessage = when (catsState.error) {
                    is ICatsContract.CatsListState.DetailsError.DataUpdateFailed ->
                        "Failed to load. Error message: ${catsState.error.cause?.message}."
                }

                Text(text = errorMessage, fontSize = 20.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = catsState.catsFiltered,
                    key = { catInfo -> catInfo.id }
                ) { catDetail ->
                    CatDetails(
                        cat = catDetail,
                        onClick = { onClick(catDetail) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CatDetails(
    cat: Cat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,  // Use your primaryContainerLight color
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer   // Use onPrimaryContainerLight for text
        ),
        elevation = CardDefaults.cardElevation(6.dp)  // Subtle elevation for depth
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Cat Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cat.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary  // Primary color for the name
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = cat.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface  // Use onSurfaceLight for description text
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Temperament Chips
            Text(
                text = "Temperament",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary  // Tertiary color for section titles
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display up to 3 temperament chips
                cat.temperament.replace(" ", "").split(",").take(3).forEach { temperament ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = temperament,
                                color = MaterialTheme.colorScheme.onSecondaryContainer, // Text color for chips
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer // Chip background
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

    }
}

@Preview
@Composable
fun PreviewCatDetails() {
    CatapultTheme(
        darkTheme = true
    ) {

        CatDetails(
            cat = Cat(
                id = "1",
                name = "Abyssinian",
                description = "The Abyss",
                life = "9-15",
                temperament = "Active, Energetic, Independent, Intelligent, Gentle",
                origin = "Egypt",
                weight = CatWeight("4.5"),
            )
        ) { }
    }
}
