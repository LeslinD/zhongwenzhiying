<script>
  export let item;
  export let media = '';
</script>

<div class="rounded-lg hover:bg-gray-200 dark:hover:bg-gray-800 hover:shadow-lg group">
  <a href={`/${item.media_type || media}/${item.id}`} class="">
    <div class="">
      <img src={`https://image.tmdb.org/t/p/w500${item.poster_path || item.profile_path}`} 
        alt={item.title || item.name || 'NO PICTURE FOUND'} 
        class="h-68 object-cover rounded-lg group-hover:scale-105 transition-all duration-300">
    </div>
    <div class="p-1 pt-2.5 pb-3">
      <h4 class="font-semibold group-hover:text-blue-500/80 truncate overflow-hidden">
        {item.original_title || item.original_name || item.name}
      </h4>
      <div class="flex justify-between">
        {#if item.release_date || item.first_air_date}
        <p class="text-sm">{item.release_date || item.first_air_date}</p>
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