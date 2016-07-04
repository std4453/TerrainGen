package terraingen.backend.nodegraph;

/**
 * Maps a value of class V to a key of class K for route choosing
 */
public interface IMapper<K, V> extends IComponent {
	K map(V value);
}
