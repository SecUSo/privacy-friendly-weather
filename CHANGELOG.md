# Changelog

<a name="v3.1.2"></a>
## [Weather (Privacy Friendly) v3.1.2](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v3.1.2) - 27 May 2024

## What's Changed
* Remove api keys and updates to API 3.0 by [@coderPaddyS](https://github.com/coderPaddyS) in https://github.com/SecUSo/privacy-friendly-weather/pull/211

## New Contributors
* [@coderPaddyS](https://github.com/coderPaddyS) made their first contribution in https://github.com/SecUSo/privacy-friendly-weather/pull/211

**Full Changelog**: https://github.com/SecUSo/privacy-friendly-weather/compare/v3.1.1...v3.1.2

[Changes][v3.1.2]


<a name="v3.1.1"></a>
## [Weather (Privacy Friendly) v3.1.1](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v3.1.1) - 23 Aug 2022

## What's Changed
* Fix German spelling in a few places. by [@swltr](https://github.com/swltr) in https://github.com/SecUSo/privacy-friendly-weather/pull/177
* Add missing preference names to BackupRestorer by [@udenr](https://github.com/udenr) in https://github.com/SecUSo/privacy-friendly-weather/pull/199
* Add missing flag to PendingIntent by [@udenr](https://github.com/udenr) in https://github.com/SecUSo/privacy-friendly-weather/pull/198

## New Contributors
* [@swltr](https://github.com/swltr) made their first contribution in https://github.com/SecUSo/privacy-friendly-weather/pull/177
* [@udenr](https://github.com/udenr) made their first contribution in https://github.com/SecUSo/privacy-friendly-weather/pull/199

**Full Changelog**: https://github.com/SecUSo/privacy-friendly-weather/compare/v3.1.0...v3.1.1

[Changes][v3.1.1]


<a name="v3.1.0"></a>
## [Weather (Privacy Friendly) v3.1.0](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v3.1.0) - 03 Aug 2022

## What's Changed
* Add Support for Backup App from F-Droid and improve app behavior by [@Kamuno](https://github.com/Kamuno) in https://github.com/SecUSo/privacy-friendly-weather/pull/194


**Full Changelog**: https://github.com/SecUSo/privacy-friendly-weather/compare/v3.0.3...v3.1.0

[Changes][v3.1.0]


<a name="v3.0.3"></a>
## [Weather (Privacy Friendly) v3.0.3](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v3.0.3) - 15 Dec 2021

- changed F-Droid descriptions
- changed About page
- translation fixes

[Changes][v3.0.3]


<a name="v3.0.2"></a>
## [Privacy Friendly Weather v3.0.2](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v3.0.2) - 05 Jun 2021

- Updated permission descriptions
- fixed potential crash for empty network errors
- fixed crash on adding city without selecting from list



[Changes][v3.0.2]


<a name="v3.0.1"></a>
## [Privacy Friendly Weather v3.0.1](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v3.0.1) - 20 May 2021

UI
- fixed small display bug on about page: made scrollable
- fixed crash on Android <5 through attribute color references

F-Droid
- incorporated translated descriptions from F-Droid repo into this repo
- updated english and german descriptions and pictures
 

[Changes][v3.0.1]


<a name="3.0"></a>
## [Privacy Friendly Weather v3.0](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/3.0) - 12 May 2021

UI
- added Toast for non-accepted keys
- fixed raindrop view and made more intuitive
- city management changes are reflected directly in list
- update animation stops after failed update
- added wind speed unit choice
- easier to read weather icons
- added rain probability to the relevant views
- new intra-day widget available
- rain amount is shown more precisely
- dark theme is implemented

Internal
- optimisation of preference management
- added Backup functionality compatible with PFA Backup App
- fixed concurrent modification error on updating views
- minimum sdk is 17 again, Backup API also works with it
- smaller bugfixes and optimisations
- tests are brought up to current version
- more requests available per day, widgets update more frequently 


[Changes][3.0]


<a name="v2.4.2"></a>
## [Privacy Friendly Weather v2.4.2](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v2.4.2) - 29 Jan 2021

Bugfixes:
- replaces 1 obsolete API key with working one
- fixed the key switching logic

[Changes][v2.4.2]


<a name="v2.4.1"></a>
## [Privacy Friendly Weather v2.4.1](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v2.4.1) - 28 Jan 2021

General
- Increased number of keys used, this is a temporary solution but should enable more "uptime"
- Fixed crash when no forecast has been able to get yet
- When using the shared keys, users can't update from OneCallAPI more than 10 times a day, this is to reduce the load 

UI
- Added graph for weekly temperature and rain expectation in Forecast view
- Course of the day header displays the currently scrolled weekday now
- Added fitting text for the first request to create an own key
- Small bugs in tutorial
- Added leading 0 in 2 places where values close to zero could be displayed without leading 0
- Changed default Course of the day view to 2day/1h, can still be changed in settings

Widgets
- Decreased update frequency to reduce API key load

[Changes][v2.4.1]


<a name="v2.4"></a>
## [Privacy Friendly Weather v2.4](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v2.4) - 31 Dec 2020

Widgets:
- Widget updates more reliable
- Widgets display last retrieved data 
- Fixed multiple bugs
- Click on Widget opens respective City in app
- Displayed details can be chosen in settings for 3 and 5 day widgets
- 3 & 5 day widgets update 3 times a day 
- 1 day widget gets updated 8 times a day

City View:
- Changed layout of information display
- Showing more information in most views
- Added wind, rain and UV data
- Rain for next 60 minutes is displayed in details
- Future 1/3-hourly forecast view has 2 options for displaying and can show more values
- Added rain radar view from rainviewer.com for cities
- Min and max temperatures for week view are coloured for easier recognition

City list:
- UI updates more fluid (e.g. when adding city to list)
- Cities sortable
- City deletion more intuitive and with description

General:
- Changed database structure to ROOM database
- Changed project to androidX
- Changed weather data update procedure
- Switched to OneCallAPI for more (detailed) information and less data usage (CurrentWeatherData API is still used when forecast is set to 5 days)
- Added prompt to insert own API key for each new/upgrading user once and on 429 (too many requests) answer.
- Updated city selection list, so roughly 3 times the number of places can be found
- Showing world map when adding cities, so different instances can be distinguished better (+ hint for the input field)
- Min and mix temperatures are displayed instead of "midday temperature" like before
- Local time used for all displayed times
- Fixed Certificate Authority issue for older devices
- Some number formatting changes
- Much more images for different weather conditions (including night versions)
- Fixed missing translations (except Japanese)
- Added Continuous Integration for the project (including some JUnit Tests)
- More squashed bugs

[Changes][v2.4]


<a name="v2.2"></a>
## [Weather (Privacy Friendly) v2.2](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v2.2) - 02 Jun 2020

- Added fastlane files for f-droid
- Added timezone support - times are now displayed in their local time
- Added adaptive launcher icon and improved legacy launcher icon
- Added improved widget support and design

- Changed city display limit from 8 to 100

- Fixed decimal numbers displaying incorrectly in some languages
- Fixed the activity being unnecessarily recreated after the dialog closed
- Fixed some database errors on migration to new version
- Fixed some layout issues

- Updated copyright notice
- Updated text on the about page

[Changes][v2.2]


<a name="v2.1.1"></a>
## [Weather (Privacy Friendly) v2.1.1](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v2.1.1) - 28 Mar 2019

- fixed some bugs and crashes
- you can now see a list of all cities you can swipe between

[Changes][v2.1.1]


<a name="v2.0"></a>
## [Privacy Friendly Weather v2.0](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v2.0) - 14 Mar 2018

- Security Improvements
- Redesign
- Integration of widgets


[Changes][v2.0]


<a name="v1.1"></a>
## [Privacy Friendly Weather v1.1](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v1.1) - 11 Nov 2016

- Issue [#12](https://github.com/SecUSo/privacy-friendly-weather/issues/12) solved
- Refinement of help
- Update of German city names


[Changes][v1.1]


<a name="v1.0"></a>
## [Privacy Friendly Weather v1.0](https://github.com/SecUSo/privacy-friendly-weather/releases/tag/v1.0) - 05 Nov 2016

Privacy Friendly Weather is an Android app that lets you watch the weather for cities and locations. This includes the current weather as well as a 5 day / 3 hour forecast. Furthermore, Privacy Friendly Weather provides a radius search. This makes it possible to find the locations around a given location which have the best weather conditions at the moment.
This app is optimized regarding the user’s privacy. It doesn’t use any tracking mechanisms, neither it displays any advertisement. It belongs to the Privacy Friendly Apps group developed by the Technische Universität Darmstadt.


[Changes][v1.0]


[v3.1.2]: https://github.com/SecUSo/privacy-friendly-weather/compare/v3.1.1...v3.1.2
[v3.1.1]: https://github.com/SecUSo/privacy-friendly-weather/compare/v3.1.0...v3.1.1
[v3.1.0]: https://github.com/SecUSo/privacy-friendly-weather/compare/v3.0.3...v3.1.0
[v3.0.3]: https://github.com/SecUSo/privacy-friendly-weather/compare/v3.0.2...v3.0.3
[v3.0.2]: https://github.com/SecUSo/privacy-friendly-weather/compare/v3.0.1...v3.0.2
[v3.0.1]: https://github.com/SecUSo/privacy-friendly-weather/compare/3.0...v3.0.1
[3.0]: https://github.com/SecUSo/privacy-friendly-weather/compare/v2.4.2...3.0
[v2.4.2]: https://github.com/SecUSo/privacy-friendly-weather/compare/v2.4.1...v2.4.2
[v2.4.1]: https://github.com/SecUSo/privacy-friendly-weather/compare/v2.4...v2.4.1
[v2.4]: https://github.com/SecUSo/privacy-friendly-weather/compare/v2.2...v2.4
[v2.2]: https://github.com/SecUSo/privacy-friendly-weather/compare/v2.1.1...v2.2
[v2.1.1]: https://github.com/SecUSo/privacy-friendly-weather/compare/v2.0...v2.1.1
[v2.0]: https://github.com/SecUSo/privacy-friendly-weather/compare/v1.1...v2.0
[v1.1]: https://github.com/SecUSo/privacy-friendly-weather/compare/v1.0...v1.1
[v1.0]: https://github.com/SecUSo/privacy-friendly-weather/tree/v1.0

<!-- Generated by https://github.com/rhysd/changelog-from-release v3.7.2 -->
