# About Android Blog Sample

This repository holds the sample source code for an Android blog app showing content from Oracle Content Management.

Please see the complete [tutorial](https://www.oracle.com/pls/topic/lookup?ctx=cloud&id=oce-android-blog-sample).

## Installation

Source code may be obtained from Github:

```
git clone https://github.com/oracle-samples/oce-android-blog-sample
```

## Running the project

Import the project in Android Studio.

Open the file `build.gradle` and provide information about your Oracle Content Management instance.  This is already set with the values below by default.

```groovy 
buildConfigField("String", "SERVER_URL", "\"https://samples.mycontentdemo.com\"")
buildConfigField("String", "CHANNEL_TOKEN", "\"47c9fb78774d4485bc7090bf7b955632\"")
```

- `SERVER_URL` - the full URL (and optional port) for your Content Management instance
- `CHANNEL_TOKEN ` - the token associated with the channel to which your assets were published

Once you have provided the necessary credential information, select an appropriate Android target device and click the Run button.

This sample may be run on-device or in an emulator. The functionality will be identical in both.

## Images

Sample images may be downloaded from [https://www.oracle.com/middleware/technologies/content-experience-downloads.html](https://www.oracle.com/middleware/technologies/content-experience-downloads.html) under a separate license.  These images are provided for reference purposes only and may not be hosted or redistributed by you.

## Contributing

This project welcomes contributions from the community. Before submitting a pull
request, please [review our contribution guide](./CONTRIBUTING.md).

## Security

Please consult the [security guide](./SECURITY.md) for our responsible security
vulnerability disclosure process.

## License

Copyright (c) 2023, Oracle and/or its affiliates and released under the
[Universal Permissive License (UPL)](https://oss.oracle.com/licenses/upl/), Version 1.0