#!/bin/bash
NOTES=$(<ReleaseNotes.txt)
/usr/bin/curl -F "status=2" -F "notify=2" -F ipa=@"/workspace/app/build/outputs/apk/debug/app-debug.apk" -F notes="$NOTES" -H "X-HockeyAppToken: 27d8525f6ff442348ed0a07e39605a0c" https://rink.hockeyapp.net/api/2/apps/upload
