
# react-native-react-native-pdf-generator
A React Native library for creating PDF files

## Currently only android suported. iOS still in development. This is a work in progress, any sugestions and help will be apreciated.

## Usage
You must manually call for `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` permissions before call `generate()`. RN provides the proper way to handle this permissions, so you can handle that in JS

```
import ReactNativePdfGenerator from 'react-native-pdf-generator'
```

### Methods
```
generate(
	albumName, // Folder's name containing the output files 
	fileName, // The file prefix name
	title, // Title below the image logo
	jsonBody, // String of json containing the body of document (See details below)
	timestampText, The timestamp prefix
	timestampFormat,
	imageName // Name of logo image file. (Just put the image file @drawable android folder),
	shareText // The title text of sharing popup
);

```
After call generate method, a popup for sharing  the file will appear.

#### JSON body
To build the body of document yout need to provide the text and font config of each line. All props all required.
```
	const jsonBody = [
		{
			text: 'Foo',
			fontSize: 20, // Integer
			fontWeight: 'bold', // One of: bold, bolditalic, italic, normal
			textAlignment: 'center' // One of: center, left, right
		},
		{
			text: '...bar',
			fontSize: 15, 
			fontWeight: 'normal', 
			textAlignment: 'right'
		},
		...
	]
```

## Installation

`$ npm install react-native-react-native-pdf-generator --save`

### Automatic installation

`$ react-native link react-native-react-native-pdf-generator`

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-react-native-pdf-generator` and add `RNReactNativePdfGenerator.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNReactNativePdfGenerator.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNReactNativePdfGeneratorPackage;` to the imports at the top of the file
  - Add `new RNReactNativePdfGeneratorPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-react-native-pdf-generator'
  	project(':react-native-react-native-pdf-generator').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-react-native-pdf-generator/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-react-native-pdf-generator')
  	```
