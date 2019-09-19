import kotlinx.coroutines.*

fun main(args: Array<String>) {
    runBlocking {
        val start: Long = System.currentTimeMillis()
        updateSomething()
        val end: Long = System.currentTimeMillis()
        println("Exucetado em: ${end - start}ms")
    }
}

/**
 * exemplo inicial de uma tarefa coom runblocking
 */
//suspend fun updateSomething() = runBlocking{
//    val local = LocalStore()
//    val remote = RemoteSource()
//
//    val localResult =     local.getSomethingLocally(1L)    //1 - executando uma tarefa no banco local
//    val result1 =    remote.getSomethingRemotelly(2L)  //2 - executando uma tarefa de repositorio remoto
//    val result2 =    remote.getSomethingRemotelly(3L)    //2 - executando uma tarefa de repositorio remoto
//
//    val merged = mergeResults(localResult, result1, result2)
//    println(merged)
//}

/**
 * Exemplo com async task sem delay
 */
//suspend fun updateSomething() = runBlocking {
//    val local = LocalStore()
//    val remote = RemoteSource()
//
//    val localResult =   async { local.getSomethingLocally(1) }   //1 - executando uma tarefa no banco local
//    val result1 =  async { remote.getSomethingRemotelly(2) }  //2 - executando uma tarefa de repositorio remoto
//    val result2 =  async { remote.getSomethingRemotelly(3) }   //2 - executando uma tarefa de repositorio remoto
//
//    val merged = mergeResults(localResult.await(), result1.await(), result2.await())
//    println(merged)
//}

/**
 * Adicionado delay na classe remota e chamada com async sendo mais eficaz
 */

suspend fun updateSomething() = runBlocking (Dispatchers.IO) {
    val local = LocalStore()
    val remote = RemoteSource()

    val localResult =   async { local.getSomethingLocally(1) }   //1 - executando uma tarefa no banco local
    val result1 =  async { remote.getSomethingRemotelly(2) }  //2 - executando uma tarefa de repositorio remoto
    val result2 =  async { remote.getSomethingRemotelly(3) }   //2 - executando uma tarefa de repositorio remoto

    val merged = mergeResults(localResult.await(), result1.await(), result2.await())
    println(merged)
}


fun mergeResults(a: String, b: String, c: String): String{
    return "$a \n$b \n$c"
}

class LocalStore{
    fun getSomethingLocally(l: Long) = "$l ${Thread.currentThread().name}"
}

class RemoteSource{
    fun getSomethingRemotelly(i: Long): String  = runBlocking{
        kotlinx.coroutines.delay(3000)
        "$i ${Thread.currentThread().name}"
    }
}