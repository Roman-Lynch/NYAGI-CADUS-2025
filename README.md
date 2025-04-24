# NYAGI-CADUS:Computer Assisted Diagnostic Ultrasound-2025

CADUS is an innovative mobile application developed as part of the NYAGI Project, designed to enhance ultrasound interpretation in underserved communities worldwide.

## Project Overview

CADUS utilizes machine learning and computer vision technologies to analyze ultrasound footage in real-time. The app alerts users to potential anomalies or areas of concern in ultrasound images, providing guidance on when to seek professional medical evaluation. By enabling easy access to health assessment support in remote areas, CADUS aims to bridge the gap in healthcare services for vulnerable populations in low income countries.

## Key Features
* Real-time analysis of ultrasound imaging
* Offline capability for use in remote areas
* User-friendly interface for healthcare workers
* Alerts for potential anomalies in ultrasound images
* Guidance for seeking professional medical evaluation

In order to run this app, you need to specify a location for Gradle in your `local.properties` file. An example of what should be included in `local.properties` is found in `local.properies.example`.

## Background

CADUS is an ongoing project developed by the 2024 and 2025 capstone teams at CU Boulder. It is part of the larger NYAGI Project, a non-profit organization dedicated to saving mothers' and babies' lives through ultrasound education in underserved communities.

## Mission

Our mission is to empower local healthcare workers in resource-poor areas with accelerated, low-cost ultrasound training, ultimately improving prenatal and emergency care in these regions.

## HOW TO RUN

Intellij is the default browser for android emulation and developing in Kotlin as it supports building and emulation all in one

In File > Project Structure > Project, ensure the SDK to be one of the following tested versions
openjdk-23
corretto-20

## EMULATION

Press the green run button to build the project
When prompted, create a new device to run the emulation on (any of the phones will work)

## PHYSICAL DEVICE

On your device in the developer settings, make sure to enable USB debugging
Build the project
Plug in your device and make sure it is being detected
Press the green run button

## OVERVIEW
This app has 4 main pages
Model Selection: This page allows you to select from different models depending on what part of the body you are scanning. Currently only the breast cancer model is up.
Settings: You can change the overall language of the app, as well as see an about us page
Gallery: Saves recent important scans for reference. In this page, you can also save the images locally to view them later
Breast Camera: Takes an image from the camera and runs it through two models
QA Model: A Yolo Nano 8 Model that places a bounding box around the ultrasound screen. This is used to isolate the camera screen for the next model
Classification Model: An EfficientNet model that classifies the ultrasound image into either malignant, benign, or neither

## FILES
The most important folder is app/src/main/java/com.google.aiedge.examples.imageclassification, which contains the bulk of the application. The program starts at MainActivity.kt and MainContent.kt

MainActivity.kt
This is the first (and only) activity that is started up in the program. There are further activity settings defined in the AndroidManifest.xml. This class initializes the MainViewModel that is then passed into Main Content.

It passes the mainViewModel and onImageProxyAnalyzed function onto MainContent.kt, which then calls DevelopmentTab(). MainContent.kt is a little redundant and could be replaced with just the DevelopmentTab page.

MainViewModel.kt
This class holds several values that are needed throughout the rest of the application, and is supported through kotlin
setting: used in ImageClassificationHelper
_navigationStack: used to create a stack of pages visited, allows for a more seamless back button
uiState + uiStateQa: values used for holding onto results from the image classification
https://developer.android.com/topic/libraries/architecture/viewmodel

/view/DevelopmentTab.kt
This file defines the pages that exist on our application. Because it all takes place in the same activity, we can just change what is rendered on the screen, rather than launching an entirely new activity with a different context. The app saves these pages into _navigationStack saved globally under the MainViewModel object.
BodyRegions: Home page, lets you select which model to use
Scan: Camera page that runs the models
Settings: allows you to change language
Gallery: Saves important images from the scan

/pages/BodyRegionsPage.kt
This is the main page that is loaded when the app is booted up. We only have one model currently, however more model buttons can be created by adding more of the SelectorOption Composable.

/pages/GalleryPage.kt
While you are scanning in the camera page, the most screenshots with the highest confidence will be saved so you can look back on them later. In the gallery page, the recent images you captured will be displayed, along with a time and label. From this page you can also save the images to be referenced later. These files are stored in /data.

/pages/SettingsPage.kt
This page allows you to change the language settings as well as see an about us page. The language is stored in the LanguageSettingsGateway object in /language. This folder also holds all the text companion objects.

/pages/BreastCameraPage.kt
The function androidOnImageAnalyzed is just a wrapper for onImageAnalyzed with the appropriate language parameter. The onImageAnalyzed function defines what happens when a frame is analyzed.

In the body of the main box object, CameraPreview is called. This function grabs an Image Proxy from the android camera and calls the androidOnImageAnalyzed function passed to it.
onImageAnalyzed is a function located in MainActivity.kt that runs two models within it
.run_QA(imageProxy) fills in the QaBox value in UiStateQa and is a bounding box around the ultrasound screen. This box is used to maximize the image being passed into the model for optimal performance
.classify(imageProxy, …) actually finds whether the image is malignant, benign, or neither, and stores it in UiState

The rest of the page displays the box and category found in the previous step

ImageClassificationHelper.kt
This file is where an image actually gets processed by the tfLite models. There are three steps
Initialize both models (initClassifier() and initQaClassifier())
Resize the bitmap to only include what is inside the QA bounding box(run_QA(), classify())
Run the resized image through the model (runQaWithTFLite(), classifyWithTFLite())

AndroidManifest.xml
This file defines all the activities that are run in the app, however we only have one activity. This is because data is automatically conserved from one activity to another, so to simplify things we have it all under the same activity. The activity is used to set different system level settings for different pages, such as screen orientation.

/navigation/HeaderBar.kt
This header bar has to work in both orientations, so there are two sections that either define it as a row or a column. The buttons take a function on declaration, as well as the color. This is so that on the camera page, a transparent color can be passed.


## Contact

For more information about CADUS or the NYAGI Project, please contact:

Sponsor Dr. Cliff Gronseth: cliff.gronseth@nyagi.org

## Acknowledgements

This project is made possible through the collaborative efforts of CU Boulder capstone teams and the NYAGI Project. 
