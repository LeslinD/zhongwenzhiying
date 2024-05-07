<script>
  export let item
  export let media = ''
  import { onMount } from 'svelte'
  let picture

  async function load() {
    const data = await fetch(
			`https://api.themoviedb.org/3/movie/${item.movie_id}?api_key=799c0bd0b2baaecc6d9301fadfaea7f7&language=en-US`
		).then(res => res.json());
    picture = data.poster_path
  }
  
  onMount(() => load())
</script>

<div class="rounded-lg hover:bg-gray-200 dark:hover:bg-gray-800 hover:shadow-lg group">
  <a href={`/等着连8080端口/${item.movie_id}`} class="">
    <div class="">
      <img src={`https://image.tmdb.org/t/p/w500${picture}`} 
        alt={item.title || 'NO PICTURE FOUND'}
        class="h-68 object-cover rounded-lg group-hover:scale-105 transition-all duration-300">
    </div>
    <div class="p-1 pt-2.5 pb-3">
      <h4 class="font-semibold group-hover:text-blue-500/80 truncate overflow-hidden">
        {item.original_title || item.title}
      </h4>
      <div class="flex justify-between">
        {#if item.releaseDate}
        <p class="text-sm">{item.releaseDate}</p>
        {/if}
        {#if item.vote_average}
        <span class="text-sm text-amber-500 px-1 font-semibold">
          &#9733; {item.vote_average.toFixed(1)}
        </span>
        {/if}
      </div>
    </div>
  </a>
</div>
