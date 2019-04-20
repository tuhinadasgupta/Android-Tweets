package edu.gwu.androidtweets.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.location.Address
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import edu.gwu.androidtweets.R
import edu.gwu.androidtweets.ui.map.MapsActivity
import edu.gwu.androidtweets.ui.tweet.TweetsActivity
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    /*
    * These variables are "lateinit" because can't actually assign a value to them until
    * onCreate() is called (e.g. we are promising to the Kotlin compiler that these will be
    * "initialized later").
    *
    * Alternative is to make them nullable and set them equal to null, but that's not as nice to
    * work with.
    *   private var username: EditText? = null
    */

    private lateinit var username: EditText

    private lateinit var password: EditText

    private lateinit var login: Button

    private lateinit var signUp: Button

    private lateinit var progressBar: ProgressBar

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    /**
     * We're creating an "anonymous class" here (e.g. we're creating a class which implements
     * TextWatcher, but not creating an explicit class).
     *
     * object : TextWatcher == "creating a new object which implements TextWatcher"
     */
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // We're calling .getText() here, but in Kotlin you can omit the "get" or "set"
            // on a getter / setter and "pretend" you're using an actual variable.
            //      username.getText() == username.text
            val inputtedUsername: String = username.text.toString().trim()
            val inputtedPassword: String = password.text.toString().trim()
            val enableButton: Boolean = inputtedUsername.isNotEmpty() && inputtedPassword.isNotEmpty()

            // Like above, this is really doing login.setEnabled(enableButton) under the hood
            login.isEnabled = enableButton
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        signUp = findViewById(R.id.signUp)

        Log.d("MainActivity", "onCreate called")


        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        progressBar = findViewById(R.id.progressBar)

        username.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

        signUp.setOnClickListener {
            val response = """
                        {
    "meta": {
      "data_type": "array",
      "item_type": "Doctor",
      "total": 41331,
      "count": 2,
      "skip": 0,
      "limit": 2
    },
    "data": [
      {
        "practices": [
          {
            "location_slug": "ca-santa-clara",
            "within_search_area": true,
            "distance": 41.69074568320229,
            "lat": 37.335451,
            "lon": -121.998482,
            "uid": "a3b62933b3e831a4a941b074da7135d1",
            "name": "Kaiser Permanente Santa Clara Homestead",
            "website": "http://mydoctor.kaiserpermanente.org/ncal/provider/farhadparivar",
            "accepts_new_patients": true,
            "insurance_uids": [
              "bluecrosscalifornia-bluecrosscapowerselecthmo",
              "multiplan-multiplanppo",
              "multiplan-phcsppo",
              "multiplan-phcsppokaiser",
              "blueshieldofcalifornia-blueshieldcabasicepobronzelevelhix",
              "blueshieldofcalifornia-blueshieldcabasicppobronzelevelhix",
              "anthem-blueviewvision",
              "healthnet-healthnetindividualfamilyppohix",
              "medicare-medicare",
              "medicaid-medicaid",
              "aetna-aetnamdbronzesilverandgoldhmo",
              "healthnet-bluegoldhmo",
              "healthnet-hmoexcelcaresilvernetwork",
              "healthnet-hmoexcelcaresilvernetworkmedicarecob",
              "gwhcigna-greatwestppo",
              "kaiserpermanente-kaiserpermanente"
            ],
            "visit_address": {
              "city": "Santa Clara",
              "lat": 37.335451,
              "lon": -121.998482,
              "state": "CA",
              "state_long": "California",
              "street": "700 Lawrence Expy",
              "zip": "95051"
            },
            "office_hours": [],
            "phones": [
              {
                "number": "1408851100",
                "type": "landline"
              }
            ],
            "languages": [
              {
                "name": "English",
                "code": "en"
              }
            ],
            "media": [
              {
                "uid": "56ea4ed308a94f3f4100008c",
                "status": "active",
                "url": "https://asset1.betterdoctor.com/images/56ea4ed308a94f3f4100008c-4_small.jpg",
                "tags": [
                  "hero"
                ],
                "versions": {
                  "small": "https://asset1.betterdoctor.com/images/56ea4ed308a94f3f4100008c-4_small.jpg",
                  "medium": "https://asset2.betterdoctor.com/images/56ea4ed308a94f3f4100008c-4_medium.jpg",
                  "large": "https://asset1.betterdoctor.com/images/56ea4ed308a94f3f4100008c-4_large.jpg",
                  "hero": "https://asset1.betterdoctor.com/images/56ea4ed308a94f3f4100008c-4_hero.jpg"
                }
              }
            ]
          }
        ],
        "educations": [],
        "profile": {
          "first_name": "Jason",
          "middle_name": "R.",
          "last_name": "Snitzer",
          "slug": "jason-snitzer",
          "title": "MD",
          "image_url": "https://asset2.betterdoctor.com/assets/general_doctor_male.png",
          "gender": "male",
          "languages": [
            {
              "name": "English",
              "code": "en"
            }
          ],
          "bio": "Dr. Jason Snitzer, MD, specialist in pediatrics, currently sees patients in Santa clara, California.\n\nDr. Snitzer is licensed to treat patients in California.\n\nDr. Snitzer has passed an automated background check which looked at elements including medical license status and malpractice screening (no history found)."
        },
        "ratings": [],
        "insurances": [
          {
            "insurance_plan": {
              "uid": "bluecrosscalifornia-bluecrosscapowerselecthmo",
              "name": "Blue Cross CA PowerSelect HMO",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "bcbs",
              "name": "BCBS"
            }
          },
          {
            "insurance_plan": {
              "uid": "multiplan-multiplanppo",
              "name": "Multiplan PPO",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "multiplan",
              "name": "Multiplan"
            }
          },
          {
            "insurance_plan": {
              "uid": "multiplan-phcsppo",
              "name": "PHCS PPO",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "multiplan",
              "name": "Multiplan"
            }
          },
          {
            "insurance_plan": {
              "uid": "multiplan-phcsppokaiser",
              "name": "PHCS PPO - Kaiser",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "multiplan",
              "name": "Multiplan"
            }
          },
          {
            "insurance_plan": {
              "uid": "blueshieldofcalifornia-blueshieldcabasicepobronzelevelhix",
              "name": "Basic EPO - Bronze level HIX",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "blueshieldofcalifornia",
              "name": "BCBS"
            }
          },
          {
            "insurance_plan": {
              "uid": "blueshieldofcalifornia-blueshieldcabasicppobronzelevelhix",
              "name": "Basic PPO - Bronze level HIX",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "blueshieldofcalifornia",
              "name": "BCBS"
            }
          },
          {
            "insurance_plan": {
              "uid": "anthem-blueviewvision",
              "name": "Blue View Vision",
              "category": [
                "vision"
              ]
            },
            "insurance_provider": {
              "uid": "anthembluecross",
              "name": "BCBS"
            }
          },
          {
            "insurance_plan": {
              "uid": "healthnet-healthnetindividualfamilyppohix",
              "name": "Health Net Individual  Family - PPO  HIX",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "healthnet",
              "name": "HealthNet"
            }
          },
          {
            "insurance_plan": {
              "uid": "medicare-medicare",
              "name": "Medicare",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "medicare",
              "name": "Medicare"
            }
          },
          {
            "insurance_plan": {
              "uid": "medicaid-medicaid",
              "name": "Medicaid",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "medicaid",
              "name": "Medicaid"
            }
          },
          {
            "insurance_plan": {
              "uid": "aetna-aetnamdbronzesilverandgoldhmo",
              "name": "MD Bronze Silver  Gold - HMO",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "aetna",
              "name": "Aetna"
            }
          },
          {
            "insurance_plan": {
              "uid": "healthnet-bluegoldhmo",
              "name": "Blue  Gold - HMO",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "healthnet",
              "name": "HealthNet"
            }
          },
          {
            "insurance_plan": {
              "uid": "healthnet-hmoexcelcaresilvernetwork",
              "name": "HMO - ExcelCare  Silver Network",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "healthnet",
              "name": "HealthNet"
            }
          },
          {
            "insurance_plan": {
              "uid": "healthnet-hmoexcelcaresilvernetworkmedicarecob",
              "name": "HMO - ExcelCare  Silver Network Medicare COB",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "healthnet",
              "name": "HealthNet"
            }
          },
          {
            "insurance_plan": {
              "uid": "gwhcigna-greatwestppo",
              "name": "Great West PPO",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "cigna",
              "name": "Cigna"
            }
          },
          {
            "insurance_plan": {
              "uid": "kaiserpermanente-kaiserpermanente",
              "name": "Kaiser Permanente",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "kaiserpermanente",
              "name": "Kaiser Permanente"
            }
          }
        ],
        "specialties": [
          {
            "uid": "pediatrician",
            "name": "Pediatrics",
            "description": "Specializes in the health of children from birth to young adulthood.",
            "category": "medical",
            "actor": "Pediatrician",
            "actors": "Pediatricians"
          }
        ],
        "licenses": [
          {
            "state": "CA"
          },
          {
            "number": "G76269",
            "state": "CA"
          }
        ],
        "uid": "001f60172493d3546f7869f4b8bad742",
        "npi": "1871670711"
      },
      {
        "practices": [
          {
            "location_slug": "ca-oakland",
            "within_search_area": true,
            "distance": 11.245006427577161,
            "lat": 37.82312,
            "lon": -122.25835,
            "uid": "e5706cec1b5ed179f5964f09bd494160",
            "name": "The Permanente Medical Group - Oakland Medical Center",
            "website": "https://mydoctor.kaiserpermanente.org/ncal/provider/davidlaw#tab%7C2%7C1%7CProfessional%7C/ncal/provider/davidlaw/about/professional?professional=aboutme.xml&ctab=About+Me&cstab=Professional&to=1&sto=0",
            "accepts_new_patients": true,
            "insurance_uids": [
              "blueshieldofcalifornia-blueshieldcabasicppobronzelevelhix",
              "healthnet-healthnetindividualfamilyppohix",
              "anthem-blueviewvision",
              "blueshieldofcalifornia-blueshieldcabasicepobronzelevelhix",
              "cigna-vision",
              "vsp-vsp",
              "healthnet-healthnetcommunitycarenetworkhmohix",
              "medicare-medicare",
              "medicaid-medicaid",
              "aetna-aetnamdbronzesilverandgoldhmo",
              "healthnet-bluegoldhmo",
              "healthnet-hmoexcelcaresilvernetwork",
              "healthnet-hmoexcelcaresilvernetworkmedicarecob",
              "anthembluecrossblueshield-golddirectaccesspluswhsa",
              "anthembluecrossblueshield-bronzedirectaccessplusgjqa",
              "healthnet-healthnetcabluegoldhmo",
              "kaiserpermanente-kaiserpermanente"
            ],
            "visit_address": {
              "city": "Oakland",
              "lat": 37.82312,
              "lon": -122.25835,
              "state": "CA",
              "state_long": "California",
              "street": "3600 Broadway",
              "zip": "94611"
            },
            "office_hours": [],
            "phones": [
              {
                "number": "5107525438",
                "type": "landline"
              }
            ],
            "languages": [
              {
                "name": "English",
                "code": "en"
              }
            ]
          }
        ],
        "educations": [],
        "profile": {
          "first_name": "Martin",
          "last_name": "Jimenez",
          "slug": "martin-jimenez",
          "title": "MD",
          "image_url": "https://asset1.betterdoctor.com/assets/general_doctor_male.png",
          "gender": "male",
          "languages": [
            {
              "name": "English",
              "code": "en"
            }
          ],
          "bio": "Dr. Martin Jimenez, MD--specialist in hospitalist and internal medicine--currently treats patients in Oakland, California.\n\nDr. Jimenez is licensed to treat patients in California.\n\nDr. Jimenez has successfully passed a background check including a medical license verification (active) and screening for malpractice history (none found)."
        },
        "ratings": [],
        "insurances": [
          {
            "insurance_plan": {
              "uid": "blueshieldofcalifornia-blueshieldcabasicppobronzelevelhix",
              "name": "Basic PPO - Bronze level HIX",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "blueshieldofcalifornia",
              "name": "BCBS"
            }
          },
          {
            "insurance_plan": {
              "uid": "healthnet-healthnetindividualfamilyppohix",
              "name": "Health Net Individual  Family - PPO  HIX",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "healthnet",
              "name": "HealthNet"
            }
          },
          {
            "insurance_plan": {
              "uid": "anthem-blueviewvision",
              "name": "Blue View Vision",
              "category": [
                "vision"
              ]
            },
            "insurance_provider": {
              "uid": "anthembluecross",
              "name": "BCBS"
            }
          },
          {
            "insurance_plan": {
              "uid": "blueshieldofcalifornia-blueshieldcabasicepobronzelevelhix",
              "name": "Basic EPO - Bronze level HIX",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "blueshieldofcalifornia",
              "name": "BCBS"
            }
          },
          {
            "insurance_plan": {
              "uid": "cigna-vision",
              "name": "Vision",
              "category": [
                "vision"
              ]
            },
            "insurance_provider": {
              "uid": "cigna",
              "name": "Cigna"
            }
          },
          {
            "insurance_plan": {
              "uid": "vsp-vsp",
              "name": "VSP",
              "category": [
                "vision"
              ]
            },
            "insurance_provider": {
              "uid": "vsp",
              "name": "VSP"
            }
          },
          {
            "insurance_plan": {
              "uid": "healthnet-healthnetcommunitycarenetworkhmohix",
              "name": "Health Net CommunityCare Network - HMO  HIX",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "healthnet",
              "name": "HealthNet"
            }
          },
          {
            "insurance_plan": {
              "uid": "medicare-medicare",
              "name": "Medicare",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "medicare",
              "name": "Medicare"
            }
          },
          {
            "insurance_plan": {
              "uid": "medicaid-medicaid",
              "name": "Medicaid",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "medicaid",
              "name": "Medicaid"
            }
          },
          {
            "insurance_plan": {
              "uid": "aetna-aetnamdbronzesilverandgoldhmo",
              "name": "MD Bronze Silver  Gold - HMO",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "aetna",
              "name": "Aetna"
            }
          },
          {
            "insurance_plan": {
              "uid": "healthnet-bluegoldhmo",
              "name": "Blue  Gold - HMO",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "healthnet",
              "name": "HealthNet"
            }
          },
          {
            "insurance_plan": {
              "uid": "healthnet-hmoexcelcaresilvernetwork",
              "name": "HMO - ExcelCare  Silver Network",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "healthnet",
              "name": "HealthNet"
            }
          },
          {
            "insurance_plan": {
              "uid": "healthnet-hmoexcelcaresilvernetworkmedicarecob",
              "name": "HMO - ExcelCare  Silver Network Medicare COB",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "healthnet",
              "name": "HealthNet"
            }
          },
          {
            "insurance_plan": {
              "uid": "anthembluecrossblueshield-golddirectaccesspluswhsa",
              "name": "Gold DirectAccess Plus with HSA",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "anthembluecrossblueshield",
              "name": "BCBS"
            }
          },
          {
            "insurance_plan": {
              "uid": "anthembluecrossblueshield-bronzedirectaccessplusgjqa",
              "name": "Bronze DirectAccess Plus - gjqa",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "anthembluecrossblueshield",
              "name": "BCBS"
            }
          },
          {
            "insurance_plan": {
              "uid": "healthnet-healthnetcabluegoldhmo",
              "name": "Health Net CA Blue  Gold HMO",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "healthnet",
              "name": "Health Net"
            }
          },
          {
            "insurance_plan": {
              "uid": "kaiserpermanente-kaiserpermanente",
              "name": "Kaiser Permanente",
              "category": [
                "medical"
              ]
            },
            "insurance_provider": {
              "uid": "kaiserpermanente",
              "name": "Kaiser Permanente"
            }
          }
        ],
        "specialties": [
          {
            "uid": "internist",
            "name": "Internal Medicine",
            "description": "Specializes in common illnesses and complex medical problems.",
            "category": "medical",
            "actor": "Internist",
            "actors": "Internists"
          },
          {
            "uid": "hospitalist",
            "name": "Hospitalist",
            "description": "Specializes in general medical care of hospitalized patients.",
            "category": "medical",
            "actor": "Hospitalist",
            "actors": "Hospitalists"
          }
        ],
        "licenses": [
          {
            "state": "CA"
          },
          {
            "number": "A78823",
            "state": "CA"
          }
        ],
        "uid": "0935b391e6759516a4ab6a8816f7cb65",
        "npi": "1821162132"
      }
    ]
  }
                    """.trimIndent()

            val responseJson = JSONObject(response)

            val dataArr = responseJson.getJSONArray("data")
            val firstData = dataArr.getJSONObject(0)

            val practiceArr = firstData.getJSONArray("practices")
            val firstPractice = practiceArr.getJSONObject(0)

            val phones = firstPractice.getJSONArray("phones")
            val firstPhone = phones.getJSONObject(0)

            val number = firstPhone.getString("number")

            Log.d("MainActivity", "Number: $number")

            val inputtedUsername: String = username.text.toString().trim()
            val inputtedPassword: String = password.text.toString().trim()

            firebaseAuth.createUserWithEmailAndPassword(
                inputtedUsername,
                inputtedPassword
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // If Sign Up is successful, Firebase automatically logs
                    // in as that user too (e.g. currentUser is set)
                    val currentUser: FirebaseUser? = firebaseAuth.currentUser
                    Toast.makeText(
                        this,
                        "Registered as: ${currentUser!!.email}",
                        Toast.LENGTH_LONG
                    ).show()

                    showNewUserNotification()
                } else {
                    val exception = task.exception
                    Toast.makeText(
                        this,
                        "Failed to register: $exception",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // This is similar to the TextWatcher -- setOnClickListener takes a View.OnClickListener
        // as a parameter, which is an **interface with only one method**, so in this special case
        // you can just use a lambda (e.g. just open brances) instead of doing
        //      object : View.OnClickListener { ... }
        login.setOnClickListener {
            val inputtedUsername: String = username.text.toString().trim()
            val inputtedPassword: String = password.text.toString().trim()

            firebaseAuth.signInWithEmailAndPassword(
                inputtedUsername,
                inputtedPassword
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAnalytics.logEvent("login_success", null)



                    val currentUser: FirebaseUser? = firebaseAuth.currentUser
                    Toast.makeText(
                        this,
                        "Logged in as: ${currentUser!!.email}",
                        Toast.LENGTH_LONG
                    ).show()

                    // User logged in, advance to the next screen
                    val intent: Intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                } else {
                    val exception = task.exception
                    Toast.makeText(
                        this,
                        "Failed to login: $exception",
                        Toast.LENGTH_LONG
                    ).show()

                    val reason: String = if (exception is FirebaseAuthInvalidCredentialsException) {
                        "invalid_credentials"
                    } else {
                        "generic_failure"
                    }

                    val bundle = Bundle()
                    bundle.putString("error_reason", reason)

                    // Tracking that the login failed and also sending
                    // along the reason why
                    firebaseAnalytics.logEvent("login_failed", bundle)
                }
            }
        }
    }

    private fun showNewUserNotification() {
        val address = Address(Locale.ENGLISH)
        address.adminArea = "Virginia"
        address.latitude = 38.8950151
        address.longitude = -77.0732913

        val tweetsIntent = Intent(this, TweetsActivity::class.java)
        tweetsIntent.putExtra("location", address)

        val tweetsPendingIntentBuilder = TaskStackBuilder.create(this)
        tweetsPendingIntentBuilder.addNextIntentWithParentStack(tweetsIntent)

        val tweetsPendingIntent = tweetsPendingIntentBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mBuilder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.drawable.ic_check_white_24dp)
            .setContentTitle("Android Tweets")
            .setContentText("Welcome to Android Tweets!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("To get started, log into the app and choose a location on the Map. You can either long press on the Map or press the button to use your current location. The app will then retrieve Tweets containing the word 'Android' near the location!"))
            .setContentIntent(tweetsPendingIntent)
            .addAction(0, "Go To Virginia", tweetsPendingIntent)


        NotificationManagerCompat.from(this).notify(0, mBuilder.build())

    }

    private fun createNotificationChannel() {
        // Only needed for Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default Notifications"
            val descriptionText = "The app's default notification set"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("default", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy called")
    }
}
