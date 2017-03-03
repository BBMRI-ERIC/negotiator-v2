# Changelog

## Version 1.0.1

- updated the product tour image for the biobank owner
- switched to Tomcat JDBC Connection pool

## Version 1.0.0

- initial release

## Version 0.0.1

- Added another URL for molgenis in case the URL for the REST interface is different
  from the URL used to create the query
- Added ClamAV settings for virus detection, see example configuration file
- Switched to Perun as authentication. The example configuration file now contains the values for perun. Set "de.samply.development" to true
  to enable the development mode, which is necessary for now.
- There is only one configuration file: `bbmri.negotiator.xml` which includes all previously existing configuration settings.
  Copy the `bbmri.negotiator.xml.example` file to `bbmri.negotiator.xml` and configure it to your needs.
- New configuration values:
    - proxy settings
    - molgenis settings (URL, resource names)