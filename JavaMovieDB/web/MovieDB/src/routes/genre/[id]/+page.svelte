<script>
  import { page } from '$app/stores'
	import { onMount } from 'svelte' 
  import { API } from '$lib/api'
  import MovieCard from '$lib/components/MovieCard.svelte'
  import currentNavBar from '$lib/currentNavBar';
  import Header from '$lib/layouts/Header.svelte';
  
  $currentNavBar = Header;
  
  const id = $page.url.pathname.split('/')[2]

  let typeMovies
  let pageNum = 1
  let pages = []

  function changePage(step) {
    pageNum += step
    if (pageNum < 1 || pageNum > 1000) {
      pageNum += -step
      return
    }
    load()
  }

  function selectPage(num) {
    if (num == pageNum) return
    if (num < 1 || num > 1000) return
    pageNum = num
    load()
  }

  function preparePages() {
    let count = pageNum - 5
    let newpages = []
    while (newpages.length < 11) {
      if (count > 0 && count < 1000) {
        newpages.push(count)
      }
      count += 1
    }
    pages = newpages
  }

  async function load() {
    preparePages()
    const data = await fetch(
      `${API}/genre?genre_id=${id}&Page=${page}`
		).then(res => res.json())
    typeMovies = data
  }
  
  onMount(() => load())
</script>

<svelte:head>
	<title>Search by Genre - MovieDB</title>
</svelte:head>

{#if typeMovies}
  <div class="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-6 gap-4 mb-6">
    {#each typeMovies as show (show.movie_id)}
      <MovieCard item={show} media=movie />
    {/each}
  </div>
{:else} 
  <h2 class="m-auto text-2xl">Loading...</h2>
{/if}

<div class="flex justify-center mb-6">
  <div class="overflow-hidden rounded-md space-x-0">
    <button on:click={() => changePage(-1)} class="px-4 h-10 bg-gray-200 dark:bg-gray-800 hover:bg-gray-300 hover:dark:bg-gray-700">
      Prev
    </button>
    {#each pages as pag (pag)}
    <button on:click={() => selectPage(pag)} 
      class="w-10 h-10
       {pag === pageNum ? "bg-blue-500/80" : "bg-gray-200 dark:bg-gray-800 hover:bg-gray-300 hover:dark:bg-gray-700"}"
    >
      {pag}
    </button>
    {/each}
    <button on:click={() => changePage(1)} class="px-4 h-10 bg-gray-200 dark:bg-gray-800 hover:bg-gray-300 hover:dark:bg-gray-700">
      Next
    </button>
  </div>
</div>