import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.FileOutputStream

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId =
            "com.github.salhe.cash_gift" + if (CashGiftsConfig.qualifiedId.isEmpty()) "" else ".${CashGiftsConfig.qualifiedId}"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        all {
            resValue("string", "app_name", CashGiftsConfig.name)

            fun buildConfigField(name: String, value: String) =
                buildConfigField("String", name, "\"$value\"")

            buildConfigField("CASH_GIFTS_DESCRIPTION", CashGiftsConfig.description)
            buildConfigField("CASH_CSV_FILE_NAME", CashGiftsConfig.csvFileName)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    sourceSets {
        all {
            assets {
                srcDir(CashGiftsConfig.csvDir)
            }
        }
    }
    applicationVariants.all {
        mergeAssetsProvider {
            dependsOn(generateCashCSVTask)
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.compose.ui:ui:${Versions.compose}")
    implementation("androidx.compose.material:material:${Versions.compose}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Versions.compose}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.activity:activity-compose:1.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.compose}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose}")

    implementation("io.github.tokenjan:pinyin4j:2.6.1")
}

val generateCashCSVTask = tasks.create("generateCashCSV") {
    doLast {
        val xlsxFile = File(project.rootDir, CashGiftsConfig.xlsxFilePath)

        operator fun Sheet.get(row: Int): Row = this.getRow(row)
        operator fun Row.get(column: Int): Cell? = this.getCell(column)

        val targetDir = File(project.projectDir, CashGiftsConfig.csvDir)
        if (!targetDir.isDirectory) {
            targetDir.mkdirs()
        }

        FileOutputStream(File(targetDir, CashGiftsConfig.csvFileName)).writer(Charsets.UTF_8).use {
            WorkbookFactory
                .create(xlsxFile)
                .sheetIterator()
                .asSequence()
                .filter {
                    it.count() >= 4
                            && "姓名" == it[0][0]?.stringCellValue
                            && "金额" == it[0][1]?.stringCellValue
                            && "地点" == it[0][2]?.stringCellValue
                            && "备注" == it[0][3]?.stringCellValue
                }
                .forEach { sheet ->
                    sheet
                        .drop(1)
                        .forEach { row ->
                            val name = row[0]?.stringCellValue
                            if (!name.isNullOrEmpty()) {
                                val cash = row[1]?.numericCellValue?.toInt() ?: 0
                                val location = row[2]?.stringCellValue ?: ""
                                val remark = row[3]?.stringCellValue ?: ""

                                it.write("$name,$cash,$location,$remark\n")
                            }
                        }
                }
        }
    }
}