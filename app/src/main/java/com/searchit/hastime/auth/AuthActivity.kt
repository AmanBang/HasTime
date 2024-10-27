package com.searchit.hastime.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.searchit.hastime.MainActivity
import com.searchit.hastime.R

class AuthActivity : AppCompatActivity() {

    private val firebaseAuthManager = FirebaseAuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("AuthenticationActivity", "onCreate:AuthActivity ")
        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        Log.i("AuthenticationActivity", "onCreate:googleSignInClient handeled "+ googleSignInClient)

        // Google Sign-In launcher
        val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
        Log.i("AuthenticationActivity", "setContent")

        // Set content to Compose layout
        setContent {
            AuthScreen(
                onGoogleSignInClick = {
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                } ,
                        backgroundImage = painterResource(id = R.drawable.auth_background), // Replace with your drawable
                iconImage = painterResource(id = R.drawable.auth_icon)
            )
        }
        Log.i("AuthenticationActivity", "setContent is set")

    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account = task.result
            firebaseAuthManager.signInWithGoogle(account) { user ->
                // Navigate to MainActivity after successful sign-in
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        } else {
            Log.e("AuthActivity", "Google Sign-In failed", task.exception)
        }
    }
//    @Composable
//    fun AuthScreen(onGoogleSignInClick: () -> Unit) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(text = "Sign in to continue")
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(onClick = onGoogleSignInClick) {
//                Text(text = "Sign in with Google")
//            }
//        }
//    }

    @Composable
    fun AuthScreen(
        onGoogleSignInClick: () -> Unit,
        backgroundImage: Painter, // Use a background image
        iconImage: Painter // Use an icon image for the top logo
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF89CFF0), Color(0xFF5D5FEE))))
        ) {
            // Background Image
            Image(
                painter = backgroundImage,
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Dark overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Adjust alpha for darkness
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon at the top
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = iconImage,
                        contentDescription = "App Icon",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

//                // App Name
//                Box(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp, vertical = 8.dp)
//                        .background(
//                            brush = Brush.horizontalGradient(
//                                colors = listOf(Color(0x99FFFFFF), Color(0x66FFFFFF)) // Semi-transparent gradient
//                            ),
//                            shape = RoundedCornerShape(12.dp)
//                        )
//                        .padding(horizontal = 16.dp, vertical = 8.dp) // Inner padding for text
//                ) {
//                    Text(
//                        text = "HasTime",
//                        style = TextStyle(
//                            color = Color.Black,
//                            fontSize = 36.sp,
//                            fontWeight = FontWeight.SemiBold,
//                            fontFamily = FontFamily.SansSerif, // Use a modern font
//                        ),
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }

                Spacer(modifier = Modifier.height(200.dp))

                // Button
                Button(
                    onClick = onGoogleSignInClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Change to white background
                    shape = RoundedCornerShape(4.dp), // Google button has sharper corners
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                        .shadow(4.dp, RoundedCornerShape(4.dp)), // Apply shadow for elevation effect
                    contentPadding = PaddingValues(horizontal = 16.dp) // Add horizontal padding
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize() // Ensure the row takes the full size of the button
                    ) {
                        // Add Google icon (make sure to import the appropriate drawable)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google), // Replace with your Google logo resource
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp), // Adjust size as needed
                            tint = Color.Unspecified // Use the original color of the icon
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                        Text(
                            text = "Sign in with Google", // Update text to match Google style
                            color = Color.Black, // Change text color to black
                            fontSize = 16.sp, // Adjust font size to match Google styling
                            maxLines = 1, // Limit to one line
                            overflow = TextOverflow.Ellipsis // Add ellipsis for overflow
                        )
                    }
                }




            }
        }
    }

}