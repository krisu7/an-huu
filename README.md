# An Hữu

* Version: 5.5.3 beta (#63)


Feel free to contact us at:

* E-mails:
    + haibison.apps[at]gmail.com


# What is this project?

This is a simple library -- a file chooser for [Android].


# CREDITS

We sincerely thank all of our friends -- who have been contributing to this
project.  We hope this project will be always useful for everyone.

* C
* Simon McCorkindale
    + [Website](http://www.aroha.mobi/)
* Philipp Crocoll
    + Author of open source project [Keepass2Android]
* Dmitry Sviridov
* And others…


# HISTORY

* Version 5.5.3 beta (#63)
    + *Initialize:* July 7th, 2014

* Version 5.5.2 (#62)
    + *Release:* March 11th, 2014
    + Change package name to `com.haibison.android.anhuu`.
    + Change prefixes for resources:
        - from `anhuu_` to `anhuu_f5be488d_`
        - from `AnHuu.` to `AnHuu.f5be488d.`
    + Design new image resources to replace some old images which were copied
      from AOSP.
    + Optimize code and make some changes to UI...

* Version 5.5.2 beta
    + *Initialize:* December 24th, 2013

* Version 5.5.1
    + *Release:* December 10th, 2013
    + Update prefix of resource IDs from `afc_` to `anhuu_`, 'Afc_' to `AnHuu_`.
    + Refactor some "preferences" classes to one class `Settings`.
    + Optimize code.

* Version 5.5
    + *Release:* December 10th, 2013
    + Change library name from `android-filechooser` to `An Hữu`. Also change
      package name from `group.pals.android.lib.ui.filechooser` to
      `com.haibison.android.lib.anhuu`.

* Version 5.4.4
    + *Release:* October 28th, 2013
    + Add `ACTION_CHOOSE` and `ACTION_SAVE`.
    + Add `EXTRA_RESULT_FILE_EXISTS` for `ACTION_SAVE`.
    + `EXTRA_RESULTS` now returns as a `Uri` for `ACTION_SAVE`. It keeps the old
      results for `ACTION_CHOOSE`.
    + Minor changes.

* Version 5.4.4 beta
    + *Initialize:* October 23rd, 2013

* Version 5.4.3
    + *Release:* October 23rd, 2013

* Version 5.4.3 beta
    + *Initialize:* September 30th, 2013

* Version 5.4.2
    + *Release:* September 27th, 2013
    + Fix light themes with dark action bar.

* Version 5.4.1
    + *Release:* September 26th, 2013
    + Fix light themes: now they use light action bar.
    + Add light themes with dark action bar.

* Version 5.4
    + *Release:* August 2nd, 2013
    + Integrate library `android-support-v7-appcompat` for action bar in APIs
      7-10.
    + Update icon sets following latest Android guidelines.
    + Optimize code.
    + Some minor changes...

* Version 5.4 beta
    + *Initialize:* June 15th, 2013

* Version 5.3
    + *Release:* June 12th, 2013
    + Add light themes (normal with action bar and dialog);
    + Fix some grammars (plural forms of verbs in multi-selection mode...);

* Version 5.2
    + *Initialize:* May 5th, 2013
    + *Release:* May 5, 2013
    + All providers: use the host's package name as authority prefix instead of
      UUID authority suffix;
    + Change coding style: use UPPER_CASE for `static final` fields and enums;

* Version 5.1
    + *Release:* April 28th, 2013
    + We're happy to announce that new version has more comfortable UI, more
      efficient and is stable. We've changed the entire architecture of the
      project. Now it uses content providers to serve requests. That's better
      approach than services.
    + Unfortunately we no longer support free-documentation. Now we're providing
      Developer Book with reasonable prices. If you could, please support us by
      purchasing the book. Visit our [official website](http://www.haibison.com)
      for further information.
    + Thank you for using our services.

* Version 5.1 beta
    + *Initialize:* October 23rd, 2012

* Version 5.0
    + *Release:* October 21st, 2012
    + We don't use Apache License 2.0 anymore. Now the library is released under
      MIT license.

* Version 4.9
    + *Release:* October 20th, 2012
    + Improves the speed of formatting file time;
    + Uses a unique filename of `SharedPreferences` instead of default
      application's `SharedPreferences`;
    + Uses `Context.MODE_MULTI_PROCESS` for storing global preferences;
    + Uses a `Runnable` + `setSelection()` to select item of list view of files.
      Because if the list view is handling data, only `setSelection()` might not
      work;
    + Updates NOTICE;

* Version 4.9 beta
    + *Initialize:* October 12th, 2012

* Version 4.8.1
    + *Release:* October 12th, 2012
    + Fixes a bug that file item does not draw properly after flinging it to
      delete files;

* Version 4.8
    + *Release:* October 12th, 2012
    + Fixes a small bug that last location or selected file will be not pushed
      into history;

* Version 4.7
    + *Release:* October 12th, 2012
    + All date formatting utilities were moved from `/IFileAdapter` to
      `/utils.DateUtils`;
    + Removes key `/FileChooserActivity._UseThemeDialog`;
    + Adds options for single tapping/ double tapping to choose files;
    + Auto remembers last location;
    + Adds key `/FileChooserActivity._SelectFile` to select a specific file on
      startup;
    + Updates new mime types;
    + UI for footer;
    + Minor changes;

* Version 4.7 beta
    + *Initialize:* October 2nd, 2012

* Version 4.6.2
    + *Release:* October 1st, 2012
    + Fixes: View does not reload after changing view type between list view and
      grid view.

* Version 4.6.1
    + *Release:* October 1st, 2012
    + Fixes: View does not reload after changing view type between list view and
      grid view.

* Version 4.6
    + *Release:* October 1st, 2012
    + Updates UI;
    + Removes deprecated method `History.push(A, A)`;
    + Keeps and shows full history to the user (wherever they have been gone
      to);
    + Removes button `Cancel` in dialogs. Users can tap `Back` button or touch
      outside of the dialogs to cancel them;
    + Moves `[/]io.LocalFile` to `[/]io.localfile.LocalFile`;
    + Fixes:
        - Issue #6 (thanks to @buckelieg);
        - Issue #10;

* Version 4.6 beta
    + *Initialize:* September 8th, 2012

* Version 4.5
    + *Release:* September 7th, 2012
    + New icons for menu `Home`, `Reload` and for file types audio, image,
      video, plain text and compressed;

* Version 4.5 beta
    + *Initialize:* August 31st, 2012

* Version 4.4
    + *Release:* August 30th, 2012
    + Added languages: Spanish, Vietnamese. Special thanks to C. - a kind friend
      who helped us translate the library into Spanish;

* Version 4.3
    + *Release:* August 29th, 2012
    + Fixed [issue #2];
    + Upgraded UI;
    + Added history viewer;
    + Improved some minor code;

* Version 4.3 beta
    + Initialization: May 19th, 2012

* Version 4.2
    + *Release:* May 15th, 2012
    + due to [Android bug #30622], so we prefix all resource names with `afc_`;
    + add small text view below location bar, to show current location's full
      name if it is truncated by the view's ellipsize property;
    + save and restore state after screen orientation changed (except selected
      items in multi-selection mode);
    + add menu `Reload`;
    + some UI fixes/ updates;

* Version 4.2 beta
    + Initialization: May 13th, 2012

* Version 4.1
    + *Release:* May 12th, 2012
    + update UI messages;
    + if the app does not have permission `WRITE_EXTERNAL_STORAGE`, notify user
      when he creates or deletes folder/ file;
    + make location bar hold buttons of directories, which user can click to go
      to;

* Version 4.1 beta
    + Initialization: May 11th, 2012

* Version 4.0 - Tablet
    + *Release:* May 11th, 2012
    + add `Home` button;
    + add grid view/ list view mode;
    + allow creating new directory;
    + allow deleting a single file/ directory by flinging its name;
    + use `android-support-v13.jar`:
        - show menu items as actions from API 11 and up;
        - support new Android layout;
    + change to new icons;
    + some minor changes;

* Version 4.0 beta
    + Initialization: May 8th, 2012

* Version 3.5
    + *Release:* May 1st, 2012
    + remove button `Cancel` (use default `Back` button of system)
    + hello May Day  :-)

* Version 3.4
    + *Release:* March 23rd, 2012
    + fix serious bug: hardcode service action name of local file provider;
      the service will be called as a remote service, which will raise
      fatal exception if there are multiple instances of the library installed
      on the device;

* Version 3.3
    + *Release:* March 22nd, 2012
    + fix bug in LoadingDialog: if the user finishes the owner activity, the
      application can crash if the dialog is going to show up or dismiss;
    + improve `FileChooserActivity`: make its height and width always fit the
      screen size in dialog theme;

* Version 3.2
    + *Release:* March 16th, 2012
    + add package `io`: `IFile` and `LocalFile`;
    + use `IFile` instead of `java.io.File`;
    + remove `FileContainer` and package `bean`;

* Version 3.1
    + *Release:* March 15th, 2012
    + add `FileProviderService`;

* Version 3.0
    + *Release:* March 15th, 2012
    + move file listing functions to external service;
    + change project name from `FileChooser` to `android-filechooser`  :-D
    + some minor changes:
        - UI messages;
        - icons;
        - make `LoadingDialog` use `AsyncTask` instead of `Thread`;
        - ...

* Version 2.0
    + *Release:* February 22nd, 2012
    + change default date format to `yyyy.MM.dd hh:mm a`;
    + try using sdcard as rootpath if it is not specified; if sdcard is not
      available, use `/`;
    + add sorter (by name/ size/ date);
    + show directories' date (last modified);

* Version 1.91
    + *Release:* February 6th, 2012
    + Add: show file time (last modified);

* Version 1.9
    + *Release:* February 6th, 2012
    + Fix: crash if cast footer of listview to `DataModel`;

* Version 1.8.2
    + *Release:* February 6th, 2012
    + enable fast scroll of the list view;

* Version 1.8.1
    + *Release:* February 5th, 2012
    + Fix: it doesn't remember the first path (rootpath) in history;

* Version 1.8
    + *Release:* February 5th, 2012
    + Shows progress dialog while listing files of a directory;
    + Adds flag max file count allowed, in case the directory has thousands of
      files, the application can harm memory. Default value is `1,024`;
    + TODO: let the user cancel the method `java.io.File.listFiles()`. It seems
      this is up to Android developers  :-)

* Version 1.7
    + *Release:* January 22nd, 2012
    + add function to check if filename (in save dialog mode) is valid or not;
    + change name `FilesAdapter` to `FileAdapter`;

* Version 1.6
    + *Release:* January 13th, 2012
    + check and warn user if save as filename is a directory;
    + when finish, return some flags for further use (in case the caller needs);

* Version 1.5
    + *Release:* January 13th, 2012
    + apply Apache License 2.0;
    + set result code to `RESULT_CANCELED` when user clicks button `Cancel`;

* Version 1.4
    + *Release:* January 8th, 2012
    + first publishing;
    + choose file(s) dialog;
    + choose file(s) and/or directory(ies) dialog;
    + save as dialog;


[Android]: http://www.android.com
[Keepass2Android]: https://keepass2android.codeplex.com/
[issue #2]: https://code.google.com/p/android-filechooser/issues/detail?id=2
[Android bug #30622]: https://code.google.com/p/android/issues/detail?id=30622
