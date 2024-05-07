<script>
  { /* 导入 */ }
  {/*onMount用于处理生命周期*/}
	import { onMount } from 'svelte' 
  import { API } from '../lib/api'
  import MovieCard from '../lib/components/MovieCard.svelte'
  import currentNavBar from '$lib/currentNavBar';
  import Header from '$lib/layouts/Header.svelte';

  $currentNavBar = Header;

  { /* 初始化 */ }
  let popularMovies
  let time = 'Highest'
  { /* 异步数据获取 */ }
  async function load() {
    let apiUrl
    if (time === 'Highest') {
      apiUrl = `${API}/home?type=popular&Page=1`
    } else if (time === 'Latest') {
      apiUrl = `${API}/home?type=latest&Page=1`
    }
    const data = await fetch(apiUrl)
      .then(res => res.json())
    popularMovies = data
  }
  
  {/*onMount在组件挂载时调用load*/}
  onMount(() => load())
</script>

<svelte:head>
	<title>PopularMovies - MovieDB</title>
</svelte:head>

<!-- 条件渲染 -->
<div class="flex align-center my-6 space-x-4">
  <h2 class="text-2xl">Popular Movies</h2>
  <select name="time" id="time"  bind:value={time} on:change={load} class="bg-gray-200 dark:bg-gray-800 rounded-md px-2 py-1">
    <option value="Highest" class="">Highest Score</option>
    <option value="Latest" class="">Latest</option>
  </select>
</div>

{#if popularMovies}
  <div class="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-6 gap-4 mb-6">
    {#each popularMovies as show (show.movie_id)}
      <MovieCard item={show} media=movie />
    {/each}
  </div>
{:else} 
  <h2 class="m-auto text-2xl">Loading...</h2>
{/if}
