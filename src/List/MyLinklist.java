package List;

import java.nio.channels.IllegalSelectorException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinklist<T> implements Iterable<T> {
	//从头节点到尾节点的引用数
	private int theSize;
	
	/**modCount
	 * 记录从链表构建以来，对链表所做出改变的次数
	 * 在迭代器内部也有一个相对应的expectedModCount，
	 * 这个字段的作用是，当创建迭代器之后，expectedModCount会和ModCount进行比较，如果不一致
	 * 说明表发生了变化
	 * 在表发生变化以后继续操作迭代器可能会发生不可预料的事情
	 * 所以expectedModCount用来判断，如果不一致会抛出一个ConcurrentModificationException
	 */
	private int modCount=0;
	//头节点
	private Node<T> beginMarker;
	//尾节点
	private Node<T> endMarker;
	//在MyLinklist里面的嵌套类 定义了双链表节点  有个一个储存节点数据， 有一个储存前一个节点，另外一个储存下一个节点
	private static class Node<T>{
		private Object data;
		private Node<T> prev;
		private Node<T> next;
		public Node(  Object d,Node<T> p,Node<T> n ) {
			data = d;
			prev = p;
			next = n;
		}
	}
	
	
	//初始化链表
	public MyLinklist() {
		super();
		doClear();
	}
	//清理链表
	public void Clear() {
		doClear();
	}
	//返回这个链表现在储存的数据个数
	public int size() {
		return theSize;
	}
	//判断此时这个链表是否为空
	public boolean isEmpty() {
		return size()==0;
	}
	public boolean add(T x) {
		add(size(),x);
		return true;
	}
	//传入要加入的节点以及索引位置 
	private void add(int idx, T x) {
		addBefore(getNode(idx,0,size()),x);
	}
	private void addBefore(Node<T> p, T x) {
		Node<T> newNode = new Node<T>(x,p.prev,p);
		newNode.prev.next = newNode;
		p.prev=newNode;
		theSize++;
		modCount++;
	}
	/**
	 * @param idx要加入的位置
	 * @param lower表的初始点
	 * @param upper表的末尾点
	 * @return
	 * 
	 */
	private Node<T> getNode(int idx, int lower, int upper) {
		Node<T> p=null;
		//查询的时候务必是size()-1，不然当查询的这个节点等于thesize时候，会直接抛出异常
		if(idx<lower || idx >upper) {
			throw new IndexOutOfBoundsException();
		}
		//判断这个要加入的位置在表的前半部分还是后半部
		if(idx<size()/2) {
			p=beginMarker.next;
			for(int i=0;i<idx;i++) {
				p=p.prev;
			}
		}else {
			p=endMarker.next;
			for(int i=size();i>idx;i--) {
				p=p.prev;
			}
		}
		return p;
	}
	private Node<T> getNode(int idx){
		return getNode(idx,0,size()-1);
		
	}
	//初始化双链表,并连接头尾节点
	private void doClear() {
		beginMarker=new Node<T>(null,null,null);
		endMarker= new Node<T>(null,beginMarker,null);
		beginMarker.next=endMarker;
		//
		theSize=0;
		//
		modCount++;
	}
	public Object get(int idx) {
		return  getNode(idx).data;
	}
	public Object set(int idx,Object newVal) {
		Node<T> p=getNode(idx);
		Object oldVal=p.data;
		p.data=newVal;
		return oldVal;
	}
	public Object remove(int idx) {
		return remove(getNode(idx));
	}
	

	private Object remove(Node<T> p) {
		p.next.prev=p.prev;
		p.prev.next=p.next;
		theSize--;
		modCount++;
		return p.data;
	}
	@Override
	public Iterator<T> iterator() {
		return new LinkedListIterator();
	}
	
	private class LinkedListIterator implements Iterator  {
		
		//索引的位置
		private Node<T> current =beginMarker.next;
		private int expectModCount =modCount;
		//判断是否使用了remove方法
		private boolean okToRemove;
		
		@Override
		public boolean hasNext() {
			return current!=endMarker;
		}

		@Override
		public Object next() {
			if(modCount!=expectModCount) {
				throw new ConcurrentModificationException();
			}
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			
			Object nextItem=current.data;
			current=current.next;
			expectModCount++;
			okToRemove=true;
			return nextItem;
		}
		
		public void remove() {
			if(modCount!=expectModCount) {
				throw new ConcurrentModificationException();
			}
			if(!okToRemove) {
				throw new IllegalSelectorException();
			}
			MyLinklist.this.remove(current.prev);
			expectModCount++;
			okToRemove=false;
		}
		
	}
	
}
