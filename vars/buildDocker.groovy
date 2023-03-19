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
                    }
                    stage('Build') {
                        println "Собираем образ и пушим в registry"
                        String tagName = git_checkout.GIT_BRANCH == "master" ? "latest" : git_checkout.GIT_BRANCH.replaceAll('/','_')
                        sh "docker build -t ${DOCKER_REGISTRY_REPO}/${appName.toLowerCase()}:${tagName} ."
                        sh "docker push ${DOCKER_REGISTRY_REPO}/${appName.toLowerCase()}:${tagName}"
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