<script>
  import { onMount } from 'svelte' 
  import { API, KEY } from '../../lib/api'
  import MovieCardAdmin from '../../lib/components/MovieCardAdmin.svelte'
  import currentNavBar from '$lib/currentNavBar';
  import HeaderAdmin from '$lib/layouts/HeaderAdmin.svelte';
  
  $currentNavBar = HeaderAdmin;

  let query = ''
  let media = 'movie'
  let page = 1
  let pages = []

  let results

  function changePage(step) {
    page += step
    if (page < 1 || page > 1000) {
      page += -step
      return
    }
    load()
  }

  function selectPage(num) {
    if (num == page) return
    if (num < 1 || num > 1000) return
    page = num
    load()
  }

  function preparePages() {
    let count = page - 5
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
    if (!query || !media) return
    const data = await fetch(
			`${API}/search/${media}${KEY}&page=${page}&language=en-US&query=${query}`
		).then(res => res.json())
    results = (data.results)
  }

  onMount(() => load())
</script>


<svelte:head>
	<title>Manage - MovieDB</title>
</svelte:head>

<div class="flex align-center my-6 gap-4">
  <h2 class="text-2xl">Search</h2>
  <select name="media" id="media" bind:value={media} on:change={load}  class="bg-gray-200 dark:bg-gray-800 rounded-md px-2 py-1">
    <option value="movie" class="">Movies</option>
    <option value="person" class="">Person</option>
  </select>
  <form on:submit|preventDefault={load} class="">
    <input type="text" class="px-2 py-1 bg-gray-50 focus:ring-blue-500/80 focus:border-blue-500 ring-offset-2 
      ring-offset-white border-gray-300 rounded-md outline-none border block w-full 
      dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white"
      placeholder="Search..." bind:value={query}
    >
  </form>
  <a href="/add/{media}"
  class="ml-auto inline-flex items-center rounded-md bg-blue-500 hover:bg-blue-500/90 px-2 py-1 text-medium text-white ring-1 ring-inset ring-blue-700/10"
  >
    + Add New
</a>
</div>


{#if results}
  <div class="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-6 gap-4 mb-6 pb-2">
    {#each results as show (show.id)}
      <MovieCardAdmin item={show} media={media} />
    {/each}
  </div>
{:else} 
  <h2 class="m-auto text-2xl pb-8">Waiting for search...</h2>
{/if}


<div class="flex justify-center mb-6">
  <div class="overflow-hidden rounded-md space-x-0">
    <button on:click={() => changePage(-1)} class="px-4 h-10 bg-gray-200 dark:bg-gray-800 hover:bg-gray-300 hover:dark:bg-gray-700">
      Prev
    </button>
    {#each pages as pag (pag)}
    <button on:click={() => selectPage(pag)} 
      class="w-10 h-10
       {pag === page ? "bg-blue-500/80" : "bg-gray-200 dark:bg-gray-800 hover:bg-gray-300 hover:dark:bg-gray-700"}"
    >
      {pag}
    </button>
    {/each}
    <button on:click={() => changePage(1)} class="px-4 h-10 bg-gray-200 dark:bg-gray-800 hover:bg-gray-300 hover:dark:bg-gray-700">
      Next
    </button>
  </div>
</div>
