<script>
  export let item
  export let media = ''
  import { onMount } from 'svelte'
  let picture

  async function load() {
    const data = await fetch(
			`https://api.themoviedb.org/3/person/${item.id}?api_key=799c0bd0b2baaecc6d9301fadfaea7f7&language=en-US`
		).then(res => res.json());
    picture = data.profile_path
  }
  
  onMount(() => load())
</script>

<div class="rounded-lg hover:bg-gray-200 dark:hover:bg-gray-800 hover:shadow-lg group">
  <a href={`/${media}/${item.id}`} class="">
    <div class="">
      <img src={`https://image.tmdb.org/t/p/original${picture}`} 
        alt={item.name || 'NO PICTURE FOUND'}
        class="h-68 object-cover rounded-lg group-hover:scale-105 transition-all duration-300">
    </div>
    <div class="p-1 pt-2.5 pb-3">
      <h4 class="font-semibold group-hover:text-blue-500/80 truncate overflow-hidden">
        {item.name}
      </h4>
    </div>
  </a>
</div>
