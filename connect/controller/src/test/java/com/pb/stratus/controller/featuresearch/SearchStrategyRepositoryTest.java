package com.pb.stratus.controller.featuresearch;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SearchStrategyRepositoryTest
{

    private SearchStrategyRepository repository;

    @Before
    public void setUp()
    {
        repository = new SearchStrategyRepository();
    }

    @Test
    public void shouldReturnUnsupportedStrategyByDefault()
    {
        SearchStrategy strat = repository.getSearchStrategy("invalid");
        assertTrue(strat instanceof UnsupportedSearchStrategy);
    }

    @Test
    public void shouldReturnRegisteredStrategyForMethod()
    {
        BaseSearchStrategy mockStrategy = mock(BaseSearchStrategy.class);
        repository.addSearchStrategy("strat1", mockStrategy);
        SearchStrategy actualStrategy =
                repository.getSearchStrategy("strat1");
        assertEquals(mockStrategy, actualStrategy);
    }

}
