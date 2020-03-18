package town.logic.delegates;

// Return T and take Y as a parameter
public interface Func<T, Y>
{
	T run(Y t);
}
