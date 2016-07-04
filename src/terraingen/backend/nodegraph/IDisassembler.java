package terraingen.backend.nodegraph;

import java.util.List;

/**
 * Disassembles an head of class I into multiple tail of class O mapped with keys of
 * class K
 */
public interface IDisassembler<I, O> {
	List<O> disassemble(I input);
}
