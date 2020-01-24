package calculator.util

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Logger

class LRUDBCache<K, V>(private val capacity: Int, owner: String) {
    private val logger = Logger.getLogger("LRUDBCache<${owner}>")
    private val map = ConcurrentHashMap<K, Node<K, V>>()
    private val head: Node<K, V> = Node(null, null)
    private val tail: Node<K, V> = Node(null, null)
    private val lock: ReentrantLock = ReentrantLock()

    init {
        head.next = tail
        tail.prev = head
    }

    operator fun get(key: K): V? {
        lock.lock()
        if (map.containsKey(key)) {
            val node = map[key]!!
            remove(node)
            addAtEnd(node)
            lock.unlock()
            return node.value
        }
        lock.unlock()
        return null
    }

    operator fun set(key: K, value: V) {
        lock.lock()
        if (map.containsKey(key)) {
            remove(map[key]!!)
        }
        val node = Node(key, value)
        map[key] = node
        addAtEnd(node)
        if (map.size > capacity) {
            val first = head.next!!
            logger.info("Removing cache element")
            remove(first)
            map.remove(first.key)
        }
        lock.unlock()
    }

    fun clear() {
        map.clear()
        head.next = tail
        tail.prev = head
    }

    private fun remove(node: Node<K, V>) {
//        lock.lock()
        val prev = node.prev!!
        val next = node.next!!
        prev.next = next
        next.prev = prev
//        lock.unlock()
    }

    private fun addAtEnd(node: Node<K, V>) {
//        lock.lock()
        val prev = tail.prev!!
        prev.next = node
        node.prev = prev
        node.next = tail
        tail.prev = node
//        lock.unlock()
    }

    data class Node<K, V>(val key: K?, val value: V?) {
        var next: Node<K, V>? = null
        var prev: Node<K, V>? = null
    }
}