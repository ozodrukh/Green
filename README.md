ConfigurationWidget

The main purpose of library is made prototyping easier by changing some configuration on the fly. Define JSON config file and when configs are changed apply changes in application.

```json
{
  "Application Information": {
    "type": "ApplicationInformationModule"
  },

  "Device Information": {
    "type": "DeviceInformationModule"
  },

  "Example Module":[
    {
      "key": "productFlavor",
      "type": "Spinner",
      "entities": {
        "#1": "ProductionMode",
        "#2": "DeveloperMode"
      }
    },
    {
      "key": "endpoint",
      "type": "EditText",
      "hint": "Endpoint url"
    }
  ],
  "Network Module":[
    {
      "type": "CheckBox",
      "key": "network-status",
      "title": "Network status",
      "value": true
    },
    {
      "type": "CheckBox",
      "key": "mobile-status",
      "title": "Mobile status"
    }
  ]
}
```
