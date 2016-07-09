#!/usr/bin/env bash

# TODO handle the error codes

###############################
# RESULT codes and meanings   #
###############################
# -128: default, nothing      #
#       occurred. Major       #
#       issues in our logic.  #
# -3: Marked for ignore       #
# -2: Not master branch       #
# -1: Detected pull request   #
# 0: Everything according to  #
#    plan.                    #
###############################
RESULT=-128

###############################
# Automatically prints the    #
# result code and exits       #
###############################
exit_check() {
    echo "Result:"${RESULT}

    case ${RESULT} in

        -128    )
            echo "There seems to be a major flaw in the logic"
            echo "Contact the devs to get this fixed"
            ;;

        -3      )
            echo "Stopping as marked for ignore"
            # The build went according to plan
            RESULT=0
            ;;

        -2      )
            echo "Stopping as not master branch"
            # The build went according to plan
            RESULT=0
            ;;

        -1      )
            echo "Stopping as PR detected"
            # The build went according to plan
            RESULT=0
            ;;

        0       )
            echo "Success!"
            ;;

        *       )
            echo "Unknown result found"
            echo "Contact the devs to get this fixed"
            ;;

    esac

    exit ${RESULT}
}

###############################
# Check Prerequisites         #
###############################
echo "Checking Prerequisites"

# Should not a pull request
if [[ ${PULL_REQUEST} != "false" ]]; then
    RESULT=-1
    exit_check
fi

# We only release for master branch
if [[ ${BRANCH} != "master" ]]; then
    RESULT=-2
    exit_check
fi

# Commits that have [ci ignore] are not to be released
if [[ $(git log --format=%B -n 1 ${COMMIT}) == *"[ci ignore]"* ]]; then
    RESULT=-3
    exit_check
fi

###############################
# Finish Prerequisites        #
###############################

# MinigameCore API version
MGC_API=$(cat .travis/apiver.txt)
SPONGE_API=$(cat .travis/sapiver.txt)

# The build number
BUILD_NUMBER=$(curl "https://api.github.com/repos/minigamecore/minigamecore/tags" | jq ".[0].name" | tr -d "\"")
BUILD_NUMBER=$((BUILD_NUMBER+1))

# The commit message
COMMIT_MESSAGE=$(curl "https://api.github.com/repos/minigamecore/minigamecore/commits/${COMMIT}" | jq ".commit.message" | tr -d "\"")

mv ${BUILD_DIR}/build/libs/MinigameCore-{all,${MGC_API}-${BUILD_NUMBER}}.jar

curl -v -X POST -d "{\"tag\": \"${BUILD_NUMBER}\",\"message\": \"MinigameCore Build ${BUILD_NUMBER}\",\"object\": \"${COMMIT}\",\"type\": \"commit\"}" --header "Content-Type:application/json" -u Flibio:${OAUTH} "https://api.github.com/repos/MinigameCore/MinigameCore/git/tags"

curl -v -X POST -d "{\"ref\": \"refs/tags/${BUILD_NUMBER}\",\"sha\": \"${COMMIT}\"}" --header "Content-Type:application/json" -u Flibio:${OAUTH} "https://api.github.com/repos/MinigameCore/MinigameCore/git/refs"

curl -v -X POST -d "{\"tag_name\": \"${BUILD_NUMBER}\",\"target_commitish\": \"master\",\"name\": \"MinigameCore Build ${BUILD_NUMBER}\", \"body\": \"${COMMIT_MESSAGE}\r\n\r\n**MinigameCore API:** ${MGC_API}\r\n\r\n**Sponge API:** ${SPONGE_API}\"}" --header "Content-Type:application/json" -u Flibio:${OAUTH} "https://api.github.com/repos/MinigameCore/MinigameCore/releases"

# Release id
RELEASE_ID=$(curl "https://api.github.com/repos/minigamecore/minigamecore/releases/latest" | jq ".id" | tr -d "\"")

curl -v -X POST --data-binary @${HOME_}/build/${TRAVIS_REPO_SLUG}/build/libs/MinigameCore-${MGC_API}-${BUILD_NUMBER}.jar --header "Content-Type:application/octet-stream" -u Flibio:${OAUTH} "https://uploads.github.com/repos/MinigameCore/MinigameCore/releases/${RELEASE_ID}/assets?name=MinigameCore-${MGC_API}-${BUILD_NUMBER}.jar"
