#!/bin/bash
set -e

VERSION="${1:-$(git describe --tags --abbrev=0)}"
VERSION="${VERSION#v}"

CHANGELOG="CHANGELOG.md"

if [ ! -f "$CHANGELOG" ]; then
    echo "Error: $CHANGELOG not found"
    exit 1
fi

SECTION=$(awk -v ver="$VERSION" '
    /^## \['"$VERSION"'\]/ { found=1; next }
    found && /^## \[/ { exit }
    found
' "$CHANGELOG")

if [ -z "$SECTION" ]; then
    echo "Error: No entry found for version $VERSION in $CHANGELOG"
    exit 1
fi

NOTES=$(echo "$SECTION" | awk '
    /^### / {
        s = $0
        if (s ~ /Fitur Baru|Fitur Dihapus|Fitur Dipulihkan|Perubahan Fitur|Optimasi|Bug Fixes|Catatan/) {
            keep = 1
        } else {
            keep = 0
        }
    }
    keep
')

if [ -z "$NOTES" ]; then
    echo "Error: No user-facing sections found for version $VERSION"
    exit 1
fi

gh release create "v$VERSION" \
    --title "v$VERSION" \
    --notes "$NOTES"
