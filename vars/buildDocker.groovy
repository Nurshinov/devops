def call(String appName) {
    DOCKER_REGISTRY_REPO = "alimzhannurshinov"
    node {
        ansiColor('xterm') {
            timestamps {
                try {
                    stage('Checkout') {
                        println "Выкачиваем репозиторий с исходным кодом"
                        git_checkout = checkout scm
                        println git_checkout
                        String tagName = git_checkout.GIT_BRANCH == "master" ? "latest" : git_checkout.GIT_BRANCH.replaceAll('/','_')
                    }
                    stage('Build') {
                        dockerImage = docker.build("${DOCKER_REGISTRY_REPO}/${appName.toLowerCase()}:${tagName}")
                    }
                    stage('Push image') {
                        withDockerRegistry([ credentialsId: "dockerhub_cred", url: "" ]) {
                            dockerImage.push()
                        }
                    }
                    cleanWs()
                } catch (exception) {
                    println "Сборка упала: " + "\n${exception}"
                    currentBuild.result = 'FAILURE'
                } finally {
                    cleanWs()
                }
            }
        }
    }
}