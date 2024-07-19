// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorBackgroundStep",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorBackgroundStep",
            targets: ["BackgroundstepPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "BackgroundstepPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/BackgroundstepPlugin"),
        .testTarget(
            name: "BackgroundstepPluginTests",
            dependencies: ["BackgroundstepPlugin"],
            path: "ios/Tests/BackgroundstepPluginTests")
    ]
)