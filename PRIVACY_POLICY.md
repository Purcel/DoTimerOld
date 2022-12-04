## DoTimer: Privacy policy

This is an open source Android app developed by Iulian Purcell. The source code is available on GitHub under the GPLv3 license; the app is also available on Google Play.

As an avid Android user myself, I take privacy very seriously. I know how irritating it is when apps collect your data without your knowledge.

I hereby state, that I have not programmed this app to collect any personally identifiable information.

### Explanation of permissions requested in the app

The list of permissions required by the app can be found in the `AndroidManifest.xml` file:

[Link to manifest](https://github.com/Purcel/DoTimer/blob/master/app/src/main/AndroidManifest.xml)

<br/>

| Permission | Why it is required |
| :---: | --- |
| `android.permission.POST_NOTIFICATIONS` | This is required to post a notification. Whitout this Alarm function won't work. |
| `android.permission.SCHEDULE_EXACT_ALARM` | Required to schedule an alarm, without this you can't set the timer. Permission automatically granted by the system; can't be revoked by user. |
| `android.permission.USE_FULL_SCREEN_INTENT` | Required to show alarm screen when alarm is ringing. Permission automatically granted by the system; can't be revoked by user. |
| `android.permission.FOREGROUND_SERVICE` | Enables the app to create foreground services that will ring the alarm. Permission automatically granted by the system; can't be revoked by user. |

 <hr style="border:1px solid gray">

If you find any security vulnerability that has been inadvertently caused by me, or have any question regarding how the app protectes your privacy, please send me an email or post a discussion on GitHub, and I will surely try to fix it/help you.

Yours sincerely,  
Iulian Purcell.  
Cluj, Transilvania.  
iulian.purcell@gmail.com
