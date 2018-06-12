# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## Version 2.0.0
### Changed
- Samply Parent 10.1

## Version 1.1.0
### Changed
- Switched to Tomcat JDBC Connection pool

## Version 1.0.1
### Changed
- Updated the product tour image for the biobank owner

## Version 1.0.0
### Added
- Initial release

## Version 0.0.1
### Added
- Added another URL for molgenis in case the URL for the REST interface is different
  from the URL used to create the query
- Added ClamAV settings for virus detection, see example configuration file
- New configuration values:
    - proxy settings
    - molgenis settings (URL, resource names)

### Changed
- Switched to Perun as authentication. The example configuration file now contains the values for perun. Set "de.samply.development" to true
  to enable the development mode, which is necessary for now.
- There is only one configuration file: `bbmri.negotiator.xml` which includes all previously existing configuration settings.
  Copy the `bbmri.negotiator.xml.example` file to `bbmri.negotiator.xml` and configure it to your needs.